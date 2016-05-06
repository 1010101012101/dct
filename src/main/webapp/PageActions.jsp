<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<table cellspacing="0">
	<vwb:CheckRequestContext context='view|info|diff|upload|share'>
		<vwb:PageExists>
			<tr>
				<td>
					<a class="action info" href="<vwb:LinkTo format='url'/>?a=info"
						title="<fmt:message key='actions.info.title' />"><fmt:message
							key="actions.info" /> </a>
				</td>
			</tr>
			<vwb:Permission permission="edit">
				<tr>
					<td>
						<a class="action pageSetting"
							href="<vwb:LinkTo format='url'/>?a=setting&method=show"
							title="<fmt:message key='actions.pagesetting.title' />"><fmt:message
								key='actions.pagesetting' /> </a>
					</td>
				</tr>
			</vwb:Permission>
			<tr>
				<td>
					<a class="action subscribe"
						href="<vwb:LinkTo format='url'/>?a=subscribe"
						title="<fmt:message key='emailnotifier.title' />"><fmt:message
							key='emailnotifier.title' /> </a>
				</td>
			</tr>
			<tr>
				<td>
					<a class="action share" href="<vwb:LinkTo format='url'/>?a=share"
						title="<fmt:message key='actions.share.title' />"><fmt:message
							key='actions.share' /> </a>
				</td>
			</tr>
		</vwb:PageExists>
	</vwb:CheckRequestContext>
</table>
