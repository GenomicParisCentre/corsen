
function include(fileName){
document.write("<script type='text/javascript' src='"+fileName+"'></script>" );
}

// Load Sun deployJava.js script
include('http://java.com/js/deployJava.js');

function launchJNLP(url, javaVersion) {

//<a href="javascript:if (!deployJava.isWebStartInstalled(&quot;1.5.0&quot;)) {if (deployJava.installLatestJRE()) {if (deployJava.launch(&quot;http://hestia.ens.fr/corsen/webstart/corsen.jnlp&quot;)) {}}} else {if (deployJava.launch(&quot;http://hestia.ens.fr/corsen/webstart/corsen.jnlp&quot;)) {}}"><img src="http://java.sun.com/products/jfc/tsc/articles/swing2d/webstart.png" /></a> Corsen Application

document.write('<a href="javascript:if (!deployJava.isWebStartInstalled(&quot;' + javaVersion + '&quot;)) {if (deployJava.installLatestJRE()) {if (deployJava.launch(&quot;' + url + '&quot;)) {}}} else {if (deployJava.launch(&quot;' + url + '&quot;)) {}}"><img src="http://java.sun.com/products/jfc/tsc/articles/swing2d/webstart.png"/></a>');
}

function launchCorsen(memory) {

  launchJNLP('@@@WEBSITE@@@/webstart/corsen-' + memory + '.jnlp', '1.5.0');
}
