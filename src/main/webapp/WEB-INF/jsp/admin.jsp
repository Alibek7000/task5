<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/tld/myTagLib.tld" prefix="myTag" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="text"/>
<html>
<head>
    <title>BikeShop - <fmt:message key="header.admin"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/style.css"/>
</head>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
<body>
<div id="sideBlock">
    <jsp:include page="/WEB-INF/jsp/sideBlock.jsp"/>
</div>
<div id="main">
    <div id="bigText">
        <c:choose>
            <c:when test="${sessionScope.user.getClass().getSimpleName() == 'Administrator'}">
                <button onClick="location.href='controller?action=showUsers'"><fmt:message
                        key="button.editUsers"/></button>
                <br>
                <button onClick="location.href='/index.jsp'"><fmt:message key="button.editCatalog"/></button>
                <br>
                <button onClick="location.href='controller?action=showCategories'"><fmt:message
                        key="button.editCategories"/></button>
                <br>
                <button onClick="location.href='controller?action=showOrders'"><fmt:message
                        key="button.viewOrders"/></button>
                <br>
                <button onClick="location.href='controller?action=deleteOldBaskets'"><fmt:message
                        key="button.deleteOldBaskets"/></button>
            </c:when>
            <c:otherwise>
                <br>
                <c:import url="controller?action=showError"/>
            </c:otherwise>
        </c:choose>
        <c:if test="${not empty param.message}">
            <fmt:message key="${param.message}"/>
        </c:if>
    </div>
</div>
</body>
</html>
