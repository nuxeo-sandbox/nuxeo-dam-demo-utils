function init_color_picker() {

  //get Image
  var img = jQuery('#color_palette')[0];
  var width =  img.width;
  var height = img.height;

  //create canvas
  var canvas = document.createElement('canvas');
  canvas.width = width;
  canvas.height = height;
  canvas.getContext('2d').drawImage(img, 0, 0, img.width, img.height);

  //Display selected values
  var valueArray = jQuery("#color_input :input").attr('value').split(',');
  for (value of valueArray) {
    if (value.length>0) display_color(value);
  }

  jQuery('#color_palette').click(function(event){

    //get Image
    var img = jQuery('#color_palette');
    var img_pos = img.offset();

    //compute coordinates
    var x = event.pageX-img_pos.left;
    var y = event.pageY-img_pos.top;

    //get pixel color
    var pixelData = canvas.getContext('2d').getImageData(x, y, 1, 1).data;
    var color = pixelData[0]*65536+pixelData[1]*256+pixelData[2];
    var colorHex = String("000000"+color.toString(16)).slice(-6).toUpperCase();

    //Add color to selection
    add_color_to_selection(colorHex);

  });
};


function add_color_to_selection(colorHex) {
    // Display color
    display_color(colorHex)
    //append value to input
    var input = jQuery("#color_input :input");
    var value = input.attr('value');
    value.length > 0 ? value = value.concat(','+colorHex) : value = value.concat(''+colorHex);
    input.attr('value',value);
};


function display_color(colorHex) {
    var root = jQuery('#selected_colors');
    var div = jQuery('<div>').
      attr('id','coucou' + Math.random()).
      attr('name',colorHex).
      addClass('selected_color').
      css('background-color','#'+colorHex);
    div.appendTo(root);
    div.click(function(event){
      var element = jQuery(event.target);
      element.remove();
      remove_color(element.attr('name'))
    });
};


function remove_color(inputColor) {
    var colorArray = jQuery("#color_input :input").attr('value').split(',');
    colorArray.splice(colorArray.indexOf(inputColor),1);
    var output = '';
    for (color of colorArray) {
      output.length > 0 ?
        output = output.concat(','+color) :
        output = output.concat(''+color);
    }
    jQuery("#color_input :input").attr('value',output);
};


