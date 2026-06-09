const MODULE_IMAGE_BASE = "/module-images";
const BRANDING_BASE = "/branding";

export const brandingAssets = {
  favicon: `${BRANDING_BASE}/favIconApp.png`,
  mainLogo: `${BRANDING_BASE}/mainLogo.webp`,
  standaloneLogo: `${BRANDING_BASE}/standaloneLogo.webp`,
  verticalLogo: `${BRANDING_BASE}/verticalLogo.webp`,
} as const;

export function moduleImageAsset(fileName: string) {
  return `${MODULE_IMAGE_BASE}/${fileName}.webp`;
}
