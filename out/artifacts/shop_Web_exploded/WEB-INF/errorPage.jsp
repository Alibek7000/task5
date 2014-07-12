<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tld/myTagLib.tld" prefix="myTag" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="text"/>
<html>
<head>
    <title>Error</title>
</head>

<body>
<div align="center">
    <img src="images/404.png" width="300"/>
    <br>
    <a href="${pageContext.request.contextPath}/"><fmt:message key="error.returnToMain"/></a>
    <br>
    <%--<c:if test="${not empty param.errorMessage}">--%>
        <fmt:message key="${param.errorMessage}"/>
    <%--</c:if>--%>
</div>
</body>
</html>
