// 传入数据生成 option
const dataList = [
   {
      name: '公务用车运行维护费',
      val: 1230,//存储数据的地方
      itemStyle: {
         color: 'rgba(0, 81, 180, 1)',
      },
   },

   {
      name: '办公费',
      val: 800,//存储数据的地方
      itemStyle: {
         color: 'rgba(255, 196, 0, 1)',
      },
   },
   {
      name: '差旅费',
      val: 500,//存储数据的地方
      itemStyle: {
         color: 'rgba(95, 144, 110, 1)',
      },
   },
];
const heightProportion = 0.2 // 柱状扇形的高度比例


// 生成扇形的曲面参数方程，用于 series-surface.parametricEquation
function getParametricEquation(startRatio, endRatio, isSelected, isHovered, k, height) {

   // 计算
   let midRatio = (startRatio + endRatio) / 3;

   let startRadian = startRatio * Math.PI * 2;
   let endRadian = endRatio * Math.PI * 2;
   let midRadian = midRatio * Math.PI * 2;

   // 如果只有一个扇形，则不实现选中效果。
   if (startRatio === 0 && endRatio === 1) {
      isSelected = false;
   }

   // 通过扇形内径/外径的值，换算出辅助参数 k（默认值 1/3）
   k = typeof k !== 'undefined' ? k : 1 / 3;

   // 计算选中效果分别在 x 轴、y 轴方向上的位移（未选中，则位移均为 0）
   let offsetX = isSelected ? Math.cos(midRadian) * 0.1 : 0;
   let offsetY = isSelected ? Math.sin(midRadian) * 0.1 : 0;

   // 计算高亮效果的放大比例（未高亮，则比例为 1）
   let hoverRate = isHovered ? 1.1 : 1;

   // 返回曲面参数方程
   return {
      u: {
         min: -Math.PI,
         max: Math.PI * 3,
         step: Math.PI / 32
      },

      v: {
         min: 0,
         max: Math.PI * 2,
         step: Math.PI / 20
      },

      x: function (u, v) {
         if (u < startRadian) {
            return offsetX + Math.cos(startRadian) * (1 + Math.cos(v) * k) * hoverRate;
         }
         if (u > endRadian) {
            return offsetX + Math.cos(endRadian) * (1 + Math.cos(v) * k) * hoverRate;
         }
         return offsetX + Math.cos(u) * (1 + Math.cos(v) * k) * hoverRate;
      },

      y: function (u, v) {
         if (u < startRadian) {
            return offsetY + Math.sin(startRadian) * (1 + Math.cos(v) * k) * hoverRate;
         }
         if (u > endRadian) {
            return offsetY + Math.sin(endRadian) * (1 + Math.cos(v) * k) * hoverRate;
         }
         return offsetY + Math.sin(u) * (1 + Math.cos(v) * k) * hoverRate;
      },

      z: function (u, v) {
        // if (u < -Math.PI * 0.5) {
        //     return Math.sin(u);
        // }
        // if (u > Math.PI * 2.5) {
        //     return Math.sin(u);
        // }
         return Math.sin(v) > 0 ? heightProportion * height : -1;
      }
   };
};

// 生成模拟 3D 饼图的配置项
function getPie3D(pieData, internalDiameterRatio) {

   let series = [];
   let sumValue = 0;
   let startValue = 0;
   let endValue = 0;
   let legendData = [];
   let linesSeries = []; // line3D模拟label指示线
   let k = typeof internalDiameterRatio !== 'undefined' ? (1 - internalDiameterRatio) / (1 + internalDiameterRatio) : 1 / 3;

   // 为每一个饼图数据，生成一个 series-surface 配置
   for (let i = 0; i < pieData.length; i++) {

      sumValue += pieData[i].value;

      let seriesItem = {
         name: typeof pieData[i].name === 'undefined' ? `series${i}` : pieData[i].name,
         type: 'surface',
         shading: 'color',
         parametric: true,
         wireframe: {
            show: false
         },
         pieData: pieData[i],
         pieStatus: {
            selected: false,
            hovered: false,
            k: k
         }
      };

      if (typeof pieData[i].itemStyle != 'undefined') {

         let itemStyle = {};

         typeof pieData[i].itemStyle.color != 'undefined' ? itemStyle.color = pieData[i].itemStyle.color : null;
         typeof pieData[i].itemStyle.opacity != 'undefined' ? itemStyle.opacity = pieData[i].itemStyle.opacity : null;

         seriesItem.itemStyle = itemStyle;
      }
      series.push(seriesItem);
   }

   // 使用上一次遍历时，计算出的数据和 sumValue，调用 getParametricEquation 函数，
   // 向每个 series-surface 传入不同的参数方程 series-surface.parametricEquation，也就是实现每一个扇形。
   for (let i = 0; i < series.length; i++) {
      endValue = startValue + series[i].pieData.value;
      // console.log(series[i]);
      series[i].pieData.startRatio = startValue / sumValue;
      series[i].pieData.endRatio = endValue / sumValue;
      series[i].parametricEquation = getParametricEquation(series[i].pieData.startRatio,
         series[i].pieData.endRatio,
         false,
         false,
         k,
        // series[i].pieData.value
        10
      );

      startValue = endValue;

      // 计算label指示线的起始和终点位置
      // let midRadian = (series[i].pieData.endRatio + series[i].pieData.startRatio) * Math.PI;
      // let posX = Math.cos(midRadian) * (1 + Math.cos(Math.PI / 2));
      // let posY = Math.sin(midRadian) * (1 + Math.cos(Math.PI / 2));
      // let posZ = Math.log(Math.abs(series[i].pieData.value + 1)) * 0.1;
      // let flag = ((midRadian >= 0 && midRadian <= Math.PI / 2) || (midRadian >= 3 * Math.PI / 2 && midRadian <= Math.PI * 2)) ? 1 : -1;
      // let color = pieData[i].itemStyle.color;
      // let turningPosArr = [posX * (1.8) + (i * 0.1 * flag) + (flag < 0 ? -0.5 : 0), posY * (1.8) + (i * 0.1 * flag) + (flag < 0 ? -0.5 : 0), posZ * (2)]
      // let endPosArr = [posX * (1.9) + (i * 0.1 * flag) + (flag < 0 ? -0.5 : 0), posY * (1.9) + (i * 0.1 * flag) + (flag < 0 ? -0.5 : 0), posZ * (6)]

      // linesSeries.push({
      //   type: 'line3D',
      //   lineStyle: {
      //       color: color,
            
      //   },
      //   data: [[posX, posY, posZ], turningPosArr, endPosArr]
      // },
      //   {
      //       type: 'scatter3D',
      //       label: {
      //         show: true,
      //         distance: 0,
      //         position: 'center',
      //         textStyle: {
      //             color: '#ffffff',
      //             backgroundColor: color,
      //             borderWidth: 2,
      //             fontSize: 14,
      //             padding: 10,
      //             borderRadius: 4,
      //         },
      //         formatter: '{b}'
      //       },
      //       symbolSize: 0,
      //       data: [{ name: series[i].name + '\n' + series[i].pieData.val, value: endPosArr }]
      //   });

      // legendData.push(series[i].name);
   }
   series = series.concat(linesSeries)

   // 最底下圆盘
  // series.push({
  //     name: 'mouseoutSeries',
  //     type: 'surface',
  //     parametric: true,
  //     wireframe: {
  //       show: false,
  //     },
  //     itemStyle: {
  //       opacity: 1,
  //       color: 'rgba(25, 93, 176, 1)',
  //     },
  //     parametricEquation: {
  //       u: {
  //           min: 0,
  //           max: Math.PI * 2,
  //           step: Math.PI / 20,
  //       },
  //       v: {
  //           min: 0,
  //           max: Math.PI,
  //           step: Math.PI / 20,
  //       },
  //       x: function (u, v) {
  //           return ((Math.sin(v) * Math.sin(u) + Math.sin(u)) / Math.PI) * 2;
  //       },
  //       y: function (u, v) {
  //           return ((Math.sin(v) * Math.cos(u) + Math.cos(u)) / Math.PI) * 2;
  //       },
  //       z: function (u, v) {
  //           return Math.cos(v) > 0 ? -0 : -1.5;
  //       },
  //     },
  // });
   return series;
}

let total = 0
dataList.forEach(item => {
   total += item.val
})
const series = getPie3D(dataList.map(item => {
   item.value = Number((item.val / total * 100).toFixed(2))
   return item
}), 0.8, 240, 28, 26, 1);

// 准备待返回的配置项，把准备好的 legendData、series 传入。
option = {
   legend: {
      tooltip: {
         show: true,
      },
      data: dataList.map(item => item.name),
      top: '5%',
      left: '5%',
      icon: 'circle',
      textStyle: {
         color: '#fff',
         fontSize: 14,
      },
   },
   animation: true,
  // title: [
  //     {
  //       x: 'center',
  //       top: '40%',
  //       text: total,
  //       textStyle: {
  //           color: '#fff',
  //           fontSize: 42,
  //           fontWeight: 'bold'
  //       },
  //     },
  //     {
  //       x: 'center',
  //       top: '48%',
  //       text: '还款总额',
  //       textStyle: {
  //           color: '#fff',
  //           fontSize: 22,
  //           fontWeight: 400
  //       },
  //     },
  // ],
   backgroundColor: '#333',
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
         distance: 180,
         alpha: 25,
         beta: 60,
         autoRotate: true, // 自动旋转
      },
   },

   series: series,
};