<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tld/myTagLib.tld" prefix="myTag" %>

<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/style.css"/>
</head>
<div class="row">
    <div id="categories">
        <c:choose>
            <c:when test="${language == 'ru_RU'}"><myTag:getCategoryListInRU/></c:when>
            <c:otherwise> <myTag:getCategoryList/></c:otherwise>
        </c:choose>
    </div>
</div>
</html>
