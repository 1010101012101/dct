<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="java.util.HashMap" %>
<%@page import="cn.vlabs.duckling.vwb.VWBContext"%>
<%@ page errorPage="/Error.jsp" %>
<%@page import="cn.vlabs.duckling.util.VOTreeUtil"%>
<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%> 
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="templates.default"/>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>    
    <title>duckling: VOTree</title>   
	<meta http-equiv="Content-Type" content="text/html,charset=GBK">
<%
	String checkedValue = request.getParameter("value");
	HashMap<String, String> checked = new HashMap<String, String>();
	if (checkedValue != null) {
	String[] values = checkedValue.split(",");
		for(String temp: values) {
			if ((temp!=null) && !(temp.trim().equals("")))
				checked.put(temp, "1");
		}
	}	
	VWBContext context = VWBContext.getContext(request);
	// 两种初始化方法
	VOTreeUtil vtutil = new VOTreeUtil(context);
    String script = vtutil.generateScripts(checked);   
    pageContext.setAttribute("baesPath", request.getContextPath());
 %>
 
 <script type="text/javascript">
	var webFXTreeConfig = {
		rootIcon        : '${baesPath}/images/votree/group.png',
		openRootIcon    : '${baesPath}/images/votree/group.png',
		folderIcon      : '${baesPath}/images/votree/group.png',
		openFolderIcon  : '${baesPath}/images/votree/group.png',
		fileIcon        : '${baesPath}/images/votree/file.png',
		iIcon           : '${baesPath}/images/votree/I.png',
		lIcon           : '${baesPath}/images/votree/L.png',
		lMinusIcon      : '${baesPath}/images/votree/Lminus.png',
		lPlusIcon       : '${baesPath}/images/votree/Lplus.png',
		tIcon           : '${baesPath}/images/votree/T.png',
		tMinusIcon      : '${baesPath}/images/votree/Tminus.png',
		tPlusIcon       : '${baesPath}/images/votree/Tplus.png',
		blankIcon       : '${baesPath}/images/votree/blank.png',
		defaultText     : 'Tree Item',
		defaultAction   : 'javascript:void(0);',
		defaultBehavior : 'classic',
		usePersistence	: true,
		defaultPrefix	: ''
	};
 </script>
 <script type="text/javascript" src="${baesPath}/jsp/emailnotify/voTree/xtree.js"></script>
 <script type="text/javascript" src="${baesPath}/jsp/emailnotify/voTree/webfxcheckboxtreeitem.js"></script>
  </head>
  
<body id="body">

<form name="voTreeForm" action="" method="post">

<table width="100%" border="0" cellspacing="0" cellpadding="0" class="tabTable">
<tr>
<th>    <h1 class="applicationlogo" align="left">&nbsp;&nbsp;<fmt:message key='votree.choose'/></h1>
</th>
</tr>
<tr>
<th width="300" height="55" scope="row" align="left"><span class="f14"><font color="#C90384">&nbsp;&nbsp;<fmt:message key='votree.list'/></font></span></th>
</tr>
<tr>
<td>
<%=script%>
</td>
</tr>
<tr>
<td width="700" class="td2" ><span class="f15">&nbsp;&nbsp;		 		
		 		<input type="hidden" name="checkedValue" />
		 		<input type="hidden" name="baseTree" />
		 		<input type="hidden" name="totalNodes" /><br><br>	
<script language="JavaScript">
var checkValue = null;
var checkValueIds = null;
var hasChecked = 0;
function submitForm() {
	var checkedString = "";
	var checkedIds = "";
	getSelectCheck();
	checkedString = filter(checkValue);
	checkedIds = filter(checkValueIds);
	var parWin = window.opener;
	parWin.document.getElementById("checkedValue").value = checkedString;
	parWin.document.getElementById("objectIds").value = checkedIds
	window.close();	
}

function filter(values) {
	var returnValue = ",";
	for (var i=0; i<values.length; i++) {
		if (returnValue.indexOf("," + values[i]  + ",") == -1)
			returnValue += values[i] + ",";
	}
	if(returnValue.length ==1) return "";
	return returnValue.substring(1, returnValue.length-1);
}

function getSelectCheck() {	
	for( itemid in webFXTreeHandler.all ) {
		if ((webFXTreeHandler.all[itemid]._checked != null) && (webFXTreeHandler.all[itemid]._checked == true))
			hasChecked++;
	}
	
	checkValue = new Array(hasChecked);
	checkValueIds = new Array(hasChecked);
	var i = 0;
	for( itemid in webFXTreeHandler.all ) { 
		if ((webFXTreeHandler.all[itemid]._checked != null) && (webFXTreeHandler.all[itemid]._checked == true)) {
			var prefix = webFXTreeHandler.all[itemid].prefix;
			var text = webFXTreeHandler.all[itemid].text;
			var id  = webFXTreeHandler.all[itemid]._sID;
			var temp = i++;
			checkValueIds[temp] = prefix + id;
			checkValue[temp] =  text;
		}		
	}	
	
}

</script>
<input type="button" name="okok" value="<fmt:message key='searchclb.confirm'/>" onclick="submitForm()"/>
<input type="button" value="<fmt:message key='version.return'/>" name="return" onclick="javascript:window.close()"/>
</span>
</td>
</tr>
<tr><td>&nbsp;</td></tr><tr><td>&nbsp;</td></tr><tr><td>&nbsp;</td></tr>
<tr><td>&nbsp;</td></tr><tr><td>&nbsp;</td></tr><tr><td>&nbsp;</td></tr>
<tr><td>&nbsp;</td></tr><tr><td>&nbsp;</td></tr><tr><td>&nbsp;</td></tr>
</table>    	
</form>
</body>
</html>
