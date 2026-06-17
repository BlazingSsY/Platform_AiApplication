import getParametricEquation from './parametric-equation'

const DATA_ITEM_HEIGHT = 2

function getPie3D(pieData) {
   let series = [];
   let sumValue = 0;
   let startValue = 0;
   let endValue = 0;
   // let legendData = [];
   // let linesSeries = []; // line3D模拟label指示线
   let k = 0.1

   for (let i = 0; i < pieData.length; i++) {
      sumValue += pieData[i].value;
      let seriesItem = {
         name: typeof pieData[i].name === 'undefined' ? `series${i}` : pieData[i].name,
         type: 'surface',
         parametric: true,
         wireframe: {
            show: false
         },
         shading: 'color',
         pieData: pieData[i],
      };

      if (typeof pieData[i].itemStyle != 'undefined') {
         let itemStyle = {};
         itemStyle.color = pieData[i].itemStyle.color || null;
         seriesItem.itemStyle = itemStyle;
      }
      series.push(seriesItem);
   }

   for (let i = 0; i < series.length; i++) {
      endValue = startValue + series[i].pieData.value
      series[i].pieData.startRatio = startValue / sumValue
      series[i].pieData.endRatio = endValue / sumValue
      series[i].parametricEquation = getParametricEquation(
         series[i].pieData.startRatio,
         series[i].pieData.endRatio,
         false,
         false,
         k,
         DATA_ITEM_HEIGHT
      )
      startValue = endValue;

      // // 计算label指示线的起始和终点位置
      // let midRadian = (series[i].pieData.endRatio + series[i].pieData.startRatio) * Math.PI;
      // let posX = Math.cos(midRadian) * (1 + Math.cos(Math.PI / 2));
      // let posY = Math.sin(midRadian) * (1 + Math.cos(Math.PI / 2));
      // let posZ = Math.log(Math.abs(series[i].pieData.value + 1)) * 0.1;
      // let flag = ((midRadian >= 0 && midRadian <= Math.PI / 2) || (midRadian >= 3 * Math.PI / 2 && midRadian <= Math.PI * 2)) ? 1 : -1;
      // let color = pieData[i].itemStyle.color;
      // let turningPosArr = [posX * (1.8) + (i * 0.1 * flag) + (flag < 0 ? -0.5 : 0), posY * (1.8) + (i * 0.1 * flag) + (flag < 0 ? -0.5 : 0), posZ * (2)]
      // let endPosArr = [posX * (1.9) + (i * 0.1 * flag) + (flag < 0 ? -0.5 : 0), posY * (1.9) + (i * 0.1 * flag) + (flag < 0 ? -0.5 : 0), posZ * (6)]

      // linesSeries.push({
      //    type: 'line3D',
      //    lineStyle: {
      //       color: color,
      //    },
      //    data: [[posX, posY, posZ], turningPosArr, endPosArr]
      // },
      //    {
      //       type: 'scatter3D',
      //       label: {
      //          show: true,
      //          distance: 0,
      //          position: 'center',
      //          textStyle: {
      //             color: '#ffffff',
      //             backgroundColor: color,
      //             borderWidth: 2,
      //             fontSize: 14,
      //             padding: 10,
      //             borderRadius: 4,
      //          },
      //          formatter: '{b}'
      //       },
      //       symbolSize: 0,
      //       data: [{ name: series[i].name + '\n' + series[i].pieData.value, value: endPosArr }]
      //    });

      // legendData.push(series[i].name);
   }
   // series = series.concat(linesSeries)

   // 最底下圆盘
   // series.push({
   //    name: 'mouseoutSeries',
   //    type: 'surface',
   //    parametric: true,
   //    wireframe: {
   //       show: false,
   //    },
   //    itemStyle: {
   //       opacity: 1,
   //       color: 'rgba(216, 233, 252, 1)',
   //    },
   //    parametricEquation: {
   //       u: {
   //          min: 0,
   //          max: Math.PI * 2,
   //          step: Math.PI / 20,
   //       },
   //       v: {
   //          min: 0,
   //          max: Math.PI,
   //          step: Math.PI / 20,
   //       },
   //       x: function (u, v) {
   //          return ((Math.sin(v) * Math.sin(u) + Math.sin(u)) / Math.PI) * 2;
   //       },
   //       y: function (u, v) {
   //          return ((Math.sin(v) * Math.cos(u) + Math.cos(u)) / Math.PI) * 2;
   //       },
   //       z: function (u, v) {
   //          return Math.cos(v) > 0 ? -0 : -1.5;
   //       },
   //    },
   // });
   return series;
}

export default (dataList) => {
   let total = 0
   dataList.forEach(item => {
      total += item.value
   })
   const series = getPie3D(
      dataList.map(item => {
         item.value = Number((item.value / total * 100).toFixed(2))
         return item
      }),
      0.8,
   );

   const option = {
      legend: {
         tooltip: {
            show: true,
         },
         data: dataList.map(item => item.name),
         top: '5%',
         left: '5%',
         icon: 'circle',
         textStyle: {
            fontSize: 14,
         },
      },
      labelLine: {
         show: true,
         lineStyle: {
            color: '#7BC0CB',
         },
      },
      label: {
         show: false,
      },
      xAxis3D: {
         min: -1.5,
         max: 1.5,
      },
      yAxis3D: {
         min: -1.5,
         max: 1.5,
      },
      zAxis3D: {
         min: -1,
         max: 1,
      },
      grid3D: {
         show: false,
         boxHeight: 4,
         bottom: '50%',
         viewControl: {
            distance: 100,
            alpha: 25,
            beta: 60,
         },
      },
      series: series,
   };
   return option
}
