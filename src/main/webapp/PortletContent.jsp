<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ page import="cn.vlabs.duckling.vwb.service.resource.Resource"%>
<fmt:setBundle basename="templates.default" />


<div id="right" class="DCT_right_body">
 <vwb:modeCheck status="Full">
		<div id="tab21" class="DCT_tabmenu">
			<div class="DCT_Button_1">
				<ul>
					<li id="poot" onmouseover="displaySubMenu()"
						onmouseout="hideSubMenu()">
						<a><fmt:message key='actions.more.icon' /> </a>
						<div id="more" class="DCT_hideoutmenu">
						<iframe id="oIF" name="oIF" src="${contextPath}/BlockMenu.html"  style="position:absolute;right:-20px;top:28px;" scrolling="no" frameborder="0">
							</iframe> 
							<table id="oTB" cellspacing="0" >
								<vwb:Permission permission="allPermission">
								<tr>
									<td>
										<a class="action info"
											href="<vwb:Link page='${vwb.viewport.resourceId}' format='url'/>/a/portalSetting?method=show"
											title="<fmt:message key='actions.portalpage.config.title' />"><fmt:message
												key="actions.portalpage.config" /> </a>
									</td>
								</tr>
								</vwb:Permission>
								<tr>
									<td>
										<a class="action pageSetting" href="<vwb:Link page='${vwb.viewport.resourceId}' format='url'/>/a/setting?method=show"
											title="<fmt:message key='actions.pagesetting.title' />"><fmt:message
												key='actions.pagesetting' /> </a>
									</td>
								</tr>

							</table>
						</div>
					</li>
				</ul>
			</div>
		</div>
	</vwb:modeCheck>
	<vwb:render content="${RenderContext.content}"/>
	<div class="DCT_clear"></div>
</div>

