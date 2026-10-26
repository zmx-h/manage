<template>
  <div class="data-details">
    <template v-if="value && typeof value === 'object'">
      <div
        v-for="(item, key) in value"
        :key="key"
        class="detail-item"
      >
        <dt>{{ labelFor(String(key), isEn) }}</dt><dd>
          <template v-if="item !== null && typeof item === 'object'">
            <DataDetails :value="item" />
          </template><template v-else>
            {{ plainText(item) || '—' }}
          </template>
        </dd>
      </div>
    </template><p v-else>
      {{ plainText(value) || '—' }}
    </p>
  </div>
</template>
<script setup>
import { labelFor } from '@/config/fields'
import { plainText } from '@/api/client'
import { useLocale } from '@/locales'
defineProps({ value: { type: [Object, Array, String, Number, Boolean], default: null } }); const { isEn } = useLocale()
</script>
