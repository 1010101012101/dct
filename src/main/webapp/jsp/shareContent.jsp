<%@ page language="java" pageEncoding="UTF-8" %>
<%@page import="cn.vlabs.duckling.vwb.VWBContext"%>
<%@page import="cn.vlabs.duckling.vwb.service.share.SharePageAccessService"%>
<%@ page import="cn.vlabs.duckling.vwb.service.dml.RenderingService"%>
<%@ page errorPage="/Error.jsp" %>

<%
    VWBContext context = VWBContext.getContext(request);
    
	String hash = request.getParameter("hash");
	String ID = request.getParameter("ID");
    
    SharePageAccessService spaf = VWBContext.getContainer().getSharePageAccessService();
    String realPageId = spaf.getPageID(Integer.parseInt(ID), hash);
    
    if (realPageId == null)
    	response.sendRedirect("error/ShareExpired.html");
    request.setAttribute("page", realPageId.trim());

    response.setContentType("text/html; charset=UTF-8");
    String pageData = context.getPage().getContent();
    
    RenderingService rs =VWBContext.getContainer().getRenderingService();
    String contentPage = rs.getHTML(context, pageData);
%>
<%=contentPage%>
<%
    	//写访问时间记录
    	spaf.UpdateShareAcl(Integer.parseInt(ID), hash);
%>