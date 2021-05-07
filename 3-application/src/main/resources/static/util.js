
var matrix = [1,0,0,1];  //矩阵， 用于做页面旋转
LOCAL_SAVE_NAME_TEMPLATE_LIST = 'template_list'

/**
编辑距离算法, 扫描文件， 会有一些字符没有识别出来， 不能判断完全相当， 用编辑距离算法看相识度
*/
function minDistance(s1, s2) {
    const len1 = s1.length
    const len2 = s2.length

    let matrix = []

    for (let i = 0; i <= len1; i++) {
        // 构造二维数组
        matrix[i] = new Array()
        for (let j = 0; j <= len2; j++) {
            // 初始化
            if (i == 0) {
                matrix[i][j] = j
            } else if (j == 0) {
                matrix[i][j] = i
            } else {
                // 进行最小值分析
                let cost = 0
                if (s1[i - 1] != s2[j - 1]) { // 相同为0，不同置1
                    cost = 1
                }
                const temp = matrix[i - 1][j - 1] + cost

                matrix[i][j] = Math.min(matrix[i - 1][j] + 1, matrix[i][j - 1] + 1, temp)
            }
        }
    }
    return matrix[len1][len2] //返回右下角的值
}


function deepClone(obj){
    if(obj == null){
        return null
    }
    let _obj = JSON.stringify(obj);
    return JSON.parse(_obj);
}

function getTanDeg(tan) {
        return Math.atan(tan);
}

/**
对单个点进行矩阵乘法， 围绕原点旋转。
point{'x':1, 'y':2}
matrix[[0, 1], [2, 3]]
*/
function matrix_rotate(m, point){

    var a = point['x']
    var b = point['y']
    var X = m[0]*a + m[1]*b
    var Y = m[2]*a + m[3]*b
    var new_point = {}
    new_point['x']=X
    new_point['y']=Y
//    console.log('point: %f, %f , new point: %f  %f',a, b , X, Y)
    return new_point
}


String.format = function(src){

       if (arguments.length == 0) return null;

       var args = Array.prototype.slice.call(arguments, 1);

       return src.replace(/\{(\d+)\}/g, function(m, i){

             return args[i];

      });

};

function uuid(len, radix) {
  var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
  var uuid = [], i;
  radix = radix || chars.length;

  if (len) {
   // Compact form
   for (i = 0; i < len; i++) uuid[i] = chars[0 | Math.random()*radix];
  } else {
   // rfc4122, version 4 form
   var r;

   // rfc4122 requires these characters
   uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
   uuid[14] = '4';

   // Fill in random data. At i==19 set the high bits of clock sequence as
   // per rfc4122, sec. 4.1.5
   for (i = 0; i < 36; i++) {
    if (!uuid[i]) {
     r = 0 | Math.random()*16;
     uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
    }
   }
  }

  return uuid.join('');
}

function load_template_list(){
    var  template_list = localStorage.getItem(LOCAL_SAVE_NAME_TEMPLATE_LIST)
    if (template_list == null || template_list == ""){
        template_list = new Array()
    }else {
        template_list = JSON.parse(template_list);
    }

    return template_list
}


/**
对 表头列元素进行排序
*/
function sort_block_by_x(a,b) {
    return a['x']-b['x'];
}
function sort_block_by_y(a,b) {
    return a['y']-b['y'];
}
function sort_block_by_left(a,b) {
    return a['left']-b['left'];
}
function sort_block_by_top(a,b) {
    return a['top']-b['top'];
}

function sort_block_by_left_top(a,b) {

    if(a['top']-b['top'] != 0){
        return a['top']-b['top'];
    }else {
        return a['left']-b['left'];
    }

}




function print_block_item(message, _blockItem){

    var topRadio = (parseFloat(_blockItem['top']) / canvas_height).toFixed(3);
    var bottomRadio = (parseFloat(_blockItem['bottom']) / canvas_height).toFixed(3);
    var leftRadio = (parseFloat(_blockItem['left']) / canvas_width).toFixed(3);
    var rightRadio = (parseFloat(_blockItem['right']) / canvas_width).toFixed(3);
    console.log("[%s] [%s]  [top=%d (%s) , bottom=%d (%s)]  [left=%d (%s), right=%d (%s)]",
                        message, _blockItem['text'] ,
                      _blockItem['top'] ,  topRadio,
                      _blockItem['bottom'] ,bottomRadio,
                      _blockItem['left'] , leftRadio,
                      _blockItem['right'] , rightRadio )

// console.log("[%s] [%s] [%s]  [top=%d(%s) , bottom=%d(%s)]  [left=%d(%s), right=%d(%s)]", message,
//                        _blockItem['id'], _blockItem['text'] ,
//                      _blockItem['top'] ,  topRadio,
//                      _blockItem['bottom'] ,bottomRadio,
//                      _blockItem['left'] , leftRadio,
//                      _blockItem['right'] , rightRadio )

}