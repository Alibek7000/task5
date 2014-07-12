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
    <title>BikeShop</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/style.css"/>
</head>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
<body>
<div id="main">
    <div id="bigText">

        <br>
        <table>
            <tr>
                <th>Id</th>
                <th><fmt:message key="clientId"/></th>
                <th><fmt:message key="orderDate"/></th>
                <th><fmt:message key="basket.total"/></th>
                <th><fmt:message key="details"/></th>
                <th><fmt:message key="sent"/></th>
            </tr>
            <c:forEach items="${sessionScope.orders}" var="order">
                <tr>
                    <td><c:out value="${order.getId()}"/></td>
                    <td><c:out value="${order.getClient().getId()}"/></td>
                    <td><c:out value="${order.getOrderDate()}"/></td>
                    <td>$<c:out value="${order.getAmount()}"/></td>
                    <td><a href="controller?action=showOrder&message=&id=${order.getId()}">SEE</a></td>
                    <td>
                        <c:choose>

                            <c:when test="${order.isSent() == 'true'}">
                                <form>
                                    <input type="checkbox" checked
                                           onClick="location.href='controller?action=sendingOrder&id=${order.getId()}&value=false'">
                                </form>
                            </c:when>
                            <c:otherwise>
                                <form>
                                    <input type="checkbox"
                                           onClick="location.href='controller?action=sendingOrder&id=${order.getId()}&value=true'">
                                </form>

                            </c:otherwise>

                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</div>
</body>
