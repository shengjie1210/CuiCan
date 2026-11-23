// 名字 ↔ 行政区代码 互查工具
// 数据来自 area-data/pcaa（与 vue-area-linkage 同源）
import { pcaa } from "area-data";

/** 在一张 code→name 表里反查 code（找不到返回空串） */
export function nameToCode(map, name) {
  if (!name) return "";
  for (const [code, n] of Object.entries(map || {})) {
    if (n === name) return code;
  }
  return "";
}

/** 给一组名字（省/市/区）反查 codes。找不到的就填空字符串。*/
export function namesToCodes(provinceName, cityName, districtName) {
  const provinceCode = nameToCode(pcaa["86"], provinceName);
  if (!provinceCode) return ["", "", ""];
  const cityCode = nameToCode(pcaa[provinceCode], cityName);
  if (!cityCode) return [provinceCode, "", ""];
  const districtCode = nameToCode(pcaa[cityCode], districtName);
  return [provinceCode, cityCode, districtCode];
}