export const appStatusOpt = [
    {
      label: '未上线',
      value: 0
    },
    {
      label: '已上线',
      value: 1
    },
    {
      label: '已下线',
      value: 2
    }
  ]

  export const appStatusLabel = (value: number) => {
    const opt = appStatusOpt.find(item => item.value === value);
    return opt ? opt.label : '';
  } 
  