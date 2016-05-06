<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<script type="text/javascript" src="<%=path%>/scripts/fckeditor/fckeditor.js?v=dct5.3"></script>	
<input id="fixDomStr" name="fixDomStr" type="hidden" value="false">
<input id="ResourceId" name="ResourceId" type="hidden" value="${editDpage.resourceId}">
<!--the input 'section' is interface & need fix -->
<input id="section" name="section" type="hidden" >
<vwb:Variable key="skin" var="skin"/>
<script type="text/javascript">
//<![CDATA[
	var oFCKeditor = new FCKeditor( 'htmlPageText' ) ;
	
	oFCKeditor.BasePath = '<vwb:Link context="plain" jsp="scripts/fckeditor/" format="url"/>';
	oFCKeditor.Height	= '450' ;
	oFCKeditor.Width  = '100%';
	oFCKeditor.Value = '${editDpage.content}';
	oFCKeditor.Config['EditorAreaDucklingCSS'] = '${skin.webPath}/skin.css';
	oFCKeditor.Config['DMLPluginXmlPath'] = oFCKeditor.BasePath+'dmlplugin.xml';
	oFCKeditor.Config['DucklingBaseHref'] ='<vwb:Link context="plain" jsp="" format="url"/>';
	oFCKeditor.Config['EditorAreaCSS'] = '<%=request.getContextPath()%>/css.css';
	oFCKeditor.Config['DucklingResourceId']='${editDpage.resourceId}';
	oFCKeditor.Config['DucklingLocales']='${locale}';
	
//]]>
	oFCKeditor.ToolbarSet = ('${useddata}'=='true')?'Duckling':'NodData';
	oFCKeditor.Create() ;
	runAutoSavePage();
	fLocker();
</script>