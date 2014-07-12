<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/tld/myTagLib.tld" prefix="myTag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="text"/>
<html>
<head>
    <title>BikeShop</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/style.css"/>
</head>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
<body>

<div id="main">
    <div id="bigText">
        <c:if test="${not empty param.message}">
            <fmt:message key="${param.message}"/><br>
        </c:if>
        <fmt:message key="order"/><c:out value=" # ${param.id}"/><br>
        <table>
            <tr>
                <th>Id</th>
                <th><fmt:message key="table.name"/></th>
                <th><fmt:message key="table.price"/></th>
                <th><fmt:message key="quantity"/></th>
            </tr>
            <c:forEach items="${sessionScope.orderItems}" var="entry">
                <tr>
                    <c:set var="item" value="${entry.key}"/>
                    <td><c:out value="${item.getId()}"/></td>
                    <td><a href="controller?action=aboutItem&id=${item.getId()}"><c:out
                            value="${item.getName()}"/></a>
                    </td>
                    <fmt:formatNumber var="price" value="${item.getPrice() * entry.value}"
                                      maxFractionDigits="2"/>
                    <td><strong>$<c:out value="${price}"/></strong></td>
                    <td><c:out value="${entry.value}"/></td>
                </tr>
            </c:forEach>
        </table>
    </div>
</div>
</body>
</html>
