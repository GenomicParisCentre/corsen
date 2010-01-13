var pf = navigator.platform;
if (!(pf.indexOf('Win')!=-1 || pf.indexOf('Linux i')!=-1)) {
  document.write(
    '<div style="float: right; margin: 10px 10px 20px 20px; padding: 5px; border: 1px solid #999; background-color: #99CCFF;">' +
      '<div style="float: left; margin-right: 10px; margin-top: 5px;"><img src="images/dialog-information.png"/></div>' +
      '<div><p><b>Warning:</b> Corsen application is not available for your platform.'+
        ' See the <a href="supportedplatforms.html">supported platforms page</a> for more informations.</p></div>' +
    '</div>');
}
