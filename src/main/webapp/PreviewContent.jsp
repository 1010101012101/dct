<%@page language="java" pageEncoding="UTF-8"%>
<%@taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />

<script type="text/javascript" src="${contextPath}/scripts/ajax/editor.js" /></script>
<fmt:message key="preview.info"/>
<form id="previewForm" name="previewForm" method="post"
	action="<vwb:EditLink format='url' />">
	<div class="DCT_tabmenu">
		<input name='pvtoed' type='button' id='pvtoed'
			onclick='javascript:submitPreviewForm(this);'
			value='<fmt:message key='editor.preview.edit.submit'/>' accesskey="e"
			title="<fmt:message key='editor.preview.edit.title'/>" />
		<input name='saveexit' type='button' id='okbutton'
			onclick='javascript:submitPreviewForm(this);'
			value="<fmt:message key='editor.preview.save.submit'/>" accesskey="s"
			title="<fmt:message key='editor.preview.save.title'/>" />
		<input name='cancel' type='button' id='cancelbutton'
			onclick='javascript:submitPreviewForm(this);'
			value="<fmt:message key='editor.preview.cancel.submit'/>"
			accesskey="q"
			title="<fmt:message key='editor.preview.cancel.title'/>" />

		<input name="action" type="hidden" id="action" />
		
		<textarea cols="80" rows="4" name="fixDomStr" id="fixDomStr" readonly="readonly" style="display: none;">
		${htmlText}
		</textarea>
		<input name="title" type="hidden" id="title"
			value='${editDpage.title}'>
		<input name="lockVersion" type="hidden" id="lockVersion"
			value='${lockVersion}'>
	</div>
</form>
<!-- div class="previewcontent"  style="float: left"> -->
<div  id="DCT_viewcontent" style="float: left">
	${editDpage.content}
</div>
