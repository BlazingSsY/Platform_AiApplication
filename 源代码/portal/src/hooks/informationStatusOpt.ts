export const informationStatusOpt = [
    {
      label: '未发布',
      value: 0
    },
    {
      label: '已发布',
      value: 1
    },
    {
      label: '已下线',
      value: 2
    }
  ]

  export const informationStatusLabel = (value: number) => {
    const opt = informationStatusOpt.find(item => item.value === value);
    return opt ? opt.label : '';
  } 
  