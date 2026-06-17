function getParametricEquation(startRatio, endRatio, isSelected, isHovered, k, height) {
   let startRadian = startRatio * Math.PI * 2;
   let endRadian = endRatio * Math.PI * 2;
   if (startRatio === 0 && endRatio === 1) {
      isSelected = false;
   }

   // 计算高亮效果的放大比例（未高亮，则比例为 1）
   let hoverRate = isHovered ? 1.1 : 1;

   // 返回曲面参数方程
   return {
      u: {
         min: 0,
         max: Math.PI * 2,
         step: Math.PI / 48
      },
      v: {
         min: 0,
         max: Math.PI * 2,
         step: Math.PI / 48
      },
      x: function (u, v) {
         if (u < startRadian) {
            return Math.cos(startRadian) * (1 + Math.cos(v) * k) * hoverRate;
         }
         if (u > endRadian) {
            return Math.cos(endRadian) * (1 + Math.cos(v) * k) * hoverRate;
         }
         return Math.cos(u) * (1 + Math.cos(v) * k) * hoverRate;
      },
      y: function (u, v) {
         if (u < startRadian) {
            return Math.sin(startRadian) * (1 + Math.cos(v) * k) * hoverRate;
         }
         if (u > endRadian) {
            return Math.sin(endRadian) * (1 + Math.cos(v) * k) * hoverRate;
         }
         return Math.sin(u) * (1 + Math.cos(v) * k) * hoverRate;
      },
      z: function (u, v) {
         if (u < -Math.PI * 0.5) {
            return Math.sin(u);
         }
         if (u > Math.PI * 2.5) {
            return Math.sin(u);
         }
         return Math.sin(v) > 0 ? height : -1;
      }
   };
};

export default getParametricEquation
