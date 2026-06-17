/* eslint-disable no-console */
export const getConstValues = (columnsConfig) => {
  return columnsConfig.reduce((result, config) => {
    if (config.isConst && typeof config.value !== 'undefined') {
      return {
        ...result,
        [config.key]: config.value,
      }
    } else {
      return result
    }
  }, {})
}

export const getOptions = (typeKey) => (entity, { dict }) => {
  if (!dict[typeKey]) {
    return []
  }
  return Object.entries(dict[typeKey])
  .map(([key, value]) => ({
    value: Number(key),
    label: value
  }))
}

export const fileNameFormatter = (fieldKey) => (entity) => {
  if (Array.isArray(entity[fieldKey])) {
    return entity[fieldKey].map(file => file.fileName).join('; ')
  }
  return ''
}

export const multiSelectFormatter = (fieldKey, dictKey) => (entity, { dict }) => {
  if (Object.keys(entity).length > 0 && Object.keys(dict).length > 0) {
    if (typeof entity[fieldKey] === 'undefined') {
      console.error(fieldKey, 'is not in data')
      return ''
    }
    if (typeof dict[dictKey] === 'undefined') {
      console.error(dictKey, 'is not in dict')
      return ''
    }
    return entity[fieldKey].map(value => dict[dictKey][value]).join('、')
  }
}
