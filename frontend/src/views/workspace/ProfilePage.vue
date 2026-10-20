<template>
  <div class="profile-page">
    <div class="page-heading">
      <div><span class="eyebrow">ACCOUNT / PERSONAL</span><h1>{{ t('profile') }}<span class="greeting-dot">.</span></h1><p>{{ t('membersHint') }}</p></div>
    </div><div class="profile-grid">
      <section class="panel profile-summary">
        <el-avatar
          :size="84"
          :src="safeUrl(user.avatarUrl)"
        >
          {{ (user.realName || user.username || 'I').slice(0,1) }}
        </el-avatar><h2>{{ user.realName || 'IECMP' }}</h2><p>{{ user.username || t('preview') }}</p><el-tag
          v-for="role in user.roles"
          :key="role"
          effect="plain"
        >
          {{ role }}
        </el-tag>
      </section><section class="panel profile-form">
        <div class="panel-heading">
          <h3>{{ t('personalInfo') }}</h3><AppIcon
            name="users"
            :size="20"
          />
        </div><el-form
          ref="profileRef"
          :model="form"
          label-position="top"
          @submit.prevent="save"
        >
          <el-form-item
            :label="t('realName')"
            prop="realName"
            :rules="[{required:true,message:t('required')}]"
          >
            <el-input
              v-model="form.realName"
              maxlength="32"
            />
          </el-form-item><div class="form-columns">
            <el-form-item :label="t('phone')">
              <el-input
                v-model="form.phone"
                maxlength="20"
              />
            </el-form-item><el-form-item
              :label="t('email')"
              prop="email"
              :rules="[{type:'email',message:pair('请输入有效邮箱','Enter a valid email'),trigger:'blur'}]"
            >
              <el-input
                v-model="form.email"
                type="email"
                maxlength="100"
              />
            </el-form-item>
          </div><el-form-item :label="pair('头像','Avatar')">
            <FileUpload
              v-model="form.avatarUrl"
              image
              biz-type="avatar"
            />
          </el-form-item><el-button
            type="primary"
            native-type="submit"
            :loading="saving"
            :disabled="preview"
          >
            {{ t('save') }}
          </el-button>
        </el-form>
      </section><section class="panel profile-security">
        <div class="panel-heading">
          <h3>{{ t('security') }}</h3><AppIcon
            name="shield"
            :size="20"
          />
        </div><p>{{ pair('定期更新密码，保护你的中心账号。修改成功后将重新登录。','Keep your account safe with an updated password. You will be signed out after changing it.') }}</p><el-button
          :disabled="preview"
          @click="operation.open('1-07')"
        >
          {{ t('changePassword') }}
        </el-button>
      </section>
    </div><ContractDialog
      ref="operation"
      @done="passwordChanged"
    />
  </div>
</template>
<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import { useWorkspace } from '@/composables/useWorkspace'
import { useLocale } from '@/locales'
import { invoke, safeUrl } from '@/api/client'
import FileUpload from '@/components/common/FileUpload.vue'
import ContractDialog from '@/components/common/ContractDialog.vue'
import AppIcon from '@/components/common/AppIcon.vue'
const user=useUserStore(); const {preview}=useWorkspace(); const {t,pair}=useLocale(); const router=useRouter(); const operation=ref(); const profileRef=ref(); const saving=ref(false); const form=reactive({realName:user.realName,phone:'',email:'',avatarUrl:user.avatarUrl})
onMounted(async()=>{if(!preview.value)try{Object.assign(form,await invoke('1-05'))}catch(e){ElMessage.error(e.message)}})
async function save(){ if(!await profileRef.value.validate().catch(()=>false)||preview.value)return; saving.value=true; try{await invoke('1-06',form); await user.fetchInfo(); ElMessage.success(t('saved'))}catch(e){ElMessage.error(e.message)}finally{saving.value=false} }
function passwordChanged(){user.reset(); ElMessage.success(t('passwordChanged')); router.push('/login')}
</script>
