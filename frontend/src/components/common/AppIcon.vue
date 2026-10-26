<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.65"
    stroke-linecap="round"
    stroke-linejoin="round"
    aria-hidden="true"
  ><path
    v-for="(path, i) in paths"
    :key="i"
    :d="path"
  /></svg>
</template>
<script setup>
import { computed } from 'vue'
const props = defineProps({ name: { type: String, default: 'grid' }, size: { type: [Number, String], default: 20 } })
const icons = {
 grid:['M3 3h7v7H3z M14 3h7v7h-7z M3 14h7v7H3z M14 14h7v7h-7z'], users:['M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2 M16 3a4 4 0 0 1 0 8 M22 21v-2a4 4 0 0 0-3-3.87 M13 7a4 4 0 1 1-8 0 4 4 0 0 1 8 0'], code:['M8 6l-6 6 6 6 M16 6l6 6-6 6 M14 3l-4 18'], wallet:['M3 6h17v15H3z M3 6V3h14v3 M16 12h5v5h-5z'], box:['M21 8l-9 5-9-5 M12 13v9 M3 7l9-5 9 5v10l-9 5-9-5z M7.5 4.5l9 5'], seat:['M4 3h16v11H4z M2 19h20 M6 14v7 M18 14v7'], award:['M12 15a7 7 0 1 0 0-14 7 7 0 0 0 0 14 M8 14l-2 9 6-3 6 3-2-9'], calendar:['M4 4h16v17H4z M8 2v4 M16 2v4 M4 10h16 M8 14h2 M14 14h2 M8 18h2'], bell:['M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9 M10 21h4'], search:['M21 21l-5-5 M18 10a8 8 0 1 1-16 0 8 8 0 0 1 16 0'], moon:['M21 12.8A9 9 0 0 1 11.2 3a9 9 0 1 0 9.8 9.8'], sun:['M12 8a4 4 0 1 1 0 8 4 4 0 0 1 0-8 M12 1v2 M12 21v2 M1 12h2 M21 12h2 M4 4l2 2 M18 18l2 2 M4 20l2-2 M18 6l2-2'], monitor:['M2 3h20v14H2z M8 22h8 M12 17v5'], globe:['M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0 M3 12h18 M12 3c5 5 5 13 0 18 M12 3c-5 5-5 13 0 18'], arrow:['M5 12h14 M13 6l6 6-6 6'], chevron:['M9 5l7 7-7 7'], plus:['M12 5v14 M5 12h14'], refresh:['M20 7v5h-5 M4 17v-5h5 M5 8a8 8 0 0 1 13-4l2 3 M19 16a8 8 0 0 1-13 4l-2-3'], menu:['M4 6h16 M4 12h16 M4 18h16'], logout:['M9 4H3v16h6 M14 8l4 4-4 4 M9 12h13'], check:['M5 12l4 4L20 5'], shield:['M12 2l9 4v6c0 6-9 10-9 10S3 18 3 12V6z M8 12l3 3 5-6'], file:['M14 2H4v20h16V8z M14 2v6h6 M8 13h8 M8 17h5'], video:['M3 5h13v14H3z M16 10l6-4v12l-6-4'], inbox:['M3 4h18v16H3z M3 13h5l2 3h4l2-3h5'], heart:['M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8L12 21l8.8-8.6a5.5 5.5 0 0 0 0-7.8'], layers:['M12 2L2 7l10 5 10-5z M2 12l10 5 10-5 M2 17l10 5 10-5'], info:['M12 8h.01 M12 12v5 M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0'], clock:['M12 7v5l3 2 M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0']
}
const paths = computed(() => icons[props.name] || icons.grid)
</script>
