<template>
  <div class="auth-page">
    <div class="auth-toolbar">
      <PreferencesControl />
    </div>
    <section class="auth-story">
      <router-link
        to="/login"
        class="brand"
      >
        <span class="brand-mark"><span /><span /><span /></span><span class="brand-copy"><strong>IECMP<span class="brand-dot">.</span></strong><small>INNOVATION & ENTREPRENEURSHIP</small></span>
      </router-link><div class="story-body">
        <span class="eyebrow">A PLACE TO BUILD. A SPACE TO GROW.</span><h1>{{ t(register ? 'registerTitle' : 'loginTitle') }}</h1><p>{{ t('loginSubtitle') }}</p><div
          class="idea-art"
          aria-hidden="true"
        >
          <div class="orbit orbit-one" /><div class="orbit orbit-two" /><div class="art-tile tile-main">
            <AppIcon
              name="layers"
              :size="56"
            />
          </div><div class="art-tile tile-code">
            <AppIcon
              name="code"
              :size="30"
            />
          </div><div class="art-tile tile-heart">
            <AppIcon
              name="heart"
              :size="29"
            />
          </div><span class="art-spark spark-one">+</span><span class="art-spark spark-two">+</span><span class="art-dot" />
        </div>
      </div><div class="story-footer">
        <span class="tiny-dot" />{{ t('brand') }}<span class="story-footer-line" />EST. FOR POSSIBILITY
      </div>
    </section>
    <section class="auth-form-area">
      <div class="auth-form-card">
        <div class="auth-mobile-brand">
          IECMP<span> .</span>
        </div><span class="eyebrow">{{ register ? 'JOIN OUR COMMUNITY' : 'WELCOME TO YOUR WORKSPACE' }}</span><h2>{{ t(register ? 'register' : admin ? 'adminLogin' : 'userLogin') }}</h2><p class="auth-description">
          {{ t('loginHint') }}
        </p>

        <el-alert
          v-if="error"
          :title="error.unimplemented ? t('unavailable') : t('requestFailed')"
          :description="error.code === 0 ? t('networkError') : error.message"
          type="warning"
          show-icon
          :closable="false"
        />
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          size="large"
          @submit.prevent="submit"
        >
          <template v-if="register">
            <div
              v-if="config?.recruitEnabled === 1"
              class="register-modes"
            >
              <el-button
                :type="registerMode === 'invite' ? 'primary' : 'default'"
                @click="switchRegisterMode('invite')"
              >
                {{ pair('邀请码注册', 'Invite code') }}
              </el-button><el-button
                :type="registerMode === 'recruit' ? 'primary' : 'default'"
                @click="switchRegisterMode('recruit')"
              >
                {{ pair('招新码注册', 'Recruit code') }}
              </el-button>
            </div>
            <p
              v-if="registerMode === 'recruit' && (config?.recruitTips || config?.recruitCodeExpireHours)"
              class="register-tip"
            >
              {{ config?.recruitTips }}<span v-if="config?.recruitCodeExpireHours">{{ pair(`注册码 ${config.recruitCodeExpireHours} 小时内有效`, `Valid for ${config.recruitCodeExpireHours} hours`) }}</span>
            </p>
            <template v-if="registerMode === 'recruit'">
              <el-form-item
                :label="pair('报名邮箱', 'Email')"
                prop="email"
              >
                <el-input
                  v-model="form.email"
                  type="email"
                  maxlength="64"
                  autocomplete="email"
                />
              </el-form-item><el-form-item
                :label="pair('注册码', 'Registration code')"
                prop="code"
              >
                <el-input
                  v-model="form.code"
                  maxlength="8"
                  :placeholder="pair('邮件中的 8 位注册码', '8-character code from email')"
                />
              </el-form-item>
            </template>
            <template v-else>
              <el-form-item
                :label="field('inviteCode')"
                prop="inviteCode"
              >
                <el-input
                  v-model="form.inviteCode"
                  maxlength="32"
                />
              </el-form-item><el-form-item
                :label="t('realName')"
                prop="realName"
              >
                <el-input
                  v-model="form.realName"
                  maxlength="32"
                  autocomplete="name"
                />
              </el-form-item><el-form-item
                :label="t('studentNo')"
                prop="studentNo"
              >
                <el-input
                  v-model="form.studentNo"
                  maxlength="20"
                />
              </el-form-item><el-form-item :label="t('major')">
                <el-input
                  v-model="form.major"
                  maxlength="50"
                />
              </el-form-item>
            </template>
          </template>
          <el-form-item
            :label="t('username')"
            prop="username"
          >
            <el-input
              v-model="form.username"
              autocomplete="username"
              :placeholder="pair('输入你的登录账号', 'Enter your username')"
              maxlength="32"
            >
              <template #prefix>
                <AppIcon
                  name="users"
                  :size="18"
                />
              </template>
            </el-input>
          </el-form-item><el-form-item
            :label="t('password')"
            prop="password"
          >
            <el-input
              v-model="form.password"
              type="password"
              autocomplete="current-password"
              show-password
              :placeholder="register ? t('passwordHint') : pair('输入密码', 'Enter your password')"
              maxlength="32"
            >
              <template #prefix>
                <AppIcon
                  name="shield"
                  :size="18"
                />
              </template>
            </el-input>
          </el-form-item>
          <el-form-item
            v-if="captchaRequired && registerMode !== 'recruit'"
            :label="t('captcha')"
            prop="captchaCode"
          >
            <div class="captcha-row">
              <el-input
                v-model="form.captchaCode"
                maxlength="8"
              /><button
                type="button"
                class="captcha-image"
                :aria-label="t('captchaRefresh')"
                @click="loadCaptcha"
              >
                <img
                  v-if="captchaImage"
                  :src="captchaImage"
                  :alt="t('captcha')"
                ><span v-else>{{ t('captchaRefresh') }}</span>
              </button>
            </div>
          </el-form-item>
          <el-button
            type="primary"
            native-type="submit"
            class="auth-submit"
            :loading="loading"
            :disabled="register && !registerEnabled"
          >
            {{ t(register ? 'submit' : 'login') }}<AppIcon
              name="arrow"
              :size="18"
            />
          </el-button>
        </el-form>
        <div class="auth-links">
          <router-link :to="register ? '/login' : '/register'">
            {{ t(register ? 'login' : 'register') }}
          </router-link><router-link
            v-if="!register"
            :to="admin ? '/login' : '/admin/login'"
          >
            {{ t(admin ? 'userLogin' : 'adminLogin') }} ↗
          </router-link>
        </div>
        <div class="auth-divider">
          <span>{{ t('agreement') }}</span>
        </div>
      </div><p class="auth-bottom">
        IECMP · {{ t('platform') }}<span>© {{ new Date().getFullYear() }}</span>
      </p>
    </section>
  </div>
</template>
<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { invoke } from '@/api/client'
import { useUserStore } from '@/store/modules/user'
import { useLocale } from '@/locales'
import AppIcon from '@/components/common/AppIcon.vue'
import PreferencesControl from '@/components/common/PreferencesControl.vue'
const props = defineProps({ admin: Boolean, register: Boolean })
const { t, pair, field } = useLocale(); const route = useRoute(); const router = useRouter(); const user = useUserStore()
const formRef = ref(); const loading = ref(false); const error = ref(null); const captchaImage = ref(''); const config = ref(null)
const form = reactive({ username: '', password: '', realName: '', studentNo: '', major: '', phone: '', inviteCode: '', email: '', code: '', captchaKey: '', captchaCode: '' })
const registerMode = ref('invite')
const registerEnabled = computed(() => !config.value || config.value.inviteEnabled === 1 || config.value.recruitEnabled === 1)
const captchaRequired = computed(() => config.value?.captchaEnabled === 1)
const requiredRule = () => ({ required: true, message: t('required'), trigger: 'blur' })
const rules = computed(() => props.register && registerMode.value === 'recruit'
  ? { email: [requiredRule(), { type: 'email', message: pair('邮箱格式不正确', 'Invalid email'), trigger: 'blur' }], code: [requiredRule(), { len: 8, message: pair('注册码为 8 位', 'Code must be 8 characters'), trigger: 'blur' }], username: [requiredRule(), { min: 4, max: 32, message: pair('账号为 4–32 位', 'Use 4–32 characters'), trigger: 'blur' }], password: [requiredRule(), { min: 6, max: 32, message: t('passwordHint'), trigger: 'blur' }, { pattern: /^(?=.*[A-Za-z])(?=.*\d).+$/, message: t('passwordHint'), trigger: 'blur' }] }
  : { username: [requiredRule(), { min: 4, max: 32, message: pair('账号为 4–32 位', 'Use 4–32 characters'), trigger: 'blur' }], password: [requiredRule(), { min: 6, max: 32, message: t('passwordHint'), trigger: 'blur' }, ...(props.register ? [{ pattern: /^(?=.*[A-Za-z])(?=.*\d).+$/, message: t('passwordHint'), trigger: 'blur' }] : [])], realName: [requiredRule()], studentNo: [requiredRule()], inviteCode: [requiredRule()], captchaCode: [requiredRule()] })
function switchRegisterMode(mode) { registerMode.value = mode; formRef.value?.clearValidate() }
async function loadCaptcha() { try { const data = await invoke('8-13'); form.captchaKey = data.captchaKey; const image = data.captchaImage || data.imageBase64 || data.image; if (image && /^data:image\/(png|jpeg|gif|webp);base64,/.test(image)) captchaImage.value = image; else if (image && /^[A-Za-z0-9+/=]+$/.test(image)) captchaImage.value = `data:image/png;base64,${image}` } catch (e) { error.value = e } }
async function submit() {
  if (!await formRef.value.validate().catch(() => false)) return
  loading.value = true; error.value = null
  try {
    if (props.register && registerMode.value === 'recruit') { await invoke('1-31', { email: form.email, code: form.code, username: form.username, password: form.password }); ElMessage.success(t('successRegister')); router.push('/login') }
    else if (props.register) { await invoke('1-23', form); ElMessage.success(t('successRegister')); router.push('/login') }
    else { const data = await invoke(props.admin ? '1-01' : '1-02', form); user.setLoginData(data); const target = String(route.query.redirect || ''); router.push(target.startsWith('/') && !target.startsWith('//') && !target.startsWith('/login') ? target : props.admin ? '/admin/dashboard' : '/home') }
  } catch (e) { error.value = e; if (captchaRequired.value) await loadCaptcha() }
  finally { loading.value = false }
}
onMounted(async () => { try { config.value = await invoke('1-22') } catch (e) { error.value = e }; if (captchaRequired.value) await loadCaptcha() })
</script>
