<%@ page language="java" import="cn.vlabs.duckling.vwb.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<fmt:setBundle basename="templates.default" />

<ul>
			<vwb:UserCheck status="notAuthenticated">
				<vwb:CheckRequestContext context='!login'>
					<vwb:Permission permission="login">
						<li>
						
							<a href="<vwb:Link page="${CSPLoginPage }" format="url"/>"
								class="DPage action login"
								title=""><fmt:message
									key="actions.login" /> </a>
						</li>
					</vwb:Permission>
				</vwb:CheckRequestContext>
			</vwb:UserCheck>
			<vwb:UserCheck status="authenticated">
			    <li>
					<a href="<vwb:Link page="${CSPLoginPage }" format="url"/>"
						class="DPage action logout"
						title="<fmt:message key='csp.personal.center'/>"> <fmt:message key='csp.personal.center'/></a>
				</li>
			</vwb:UserCheck>

</ul>