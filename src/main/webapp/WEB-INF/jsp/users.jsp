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
    <title>Shop - catalog</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/style.css"/>
</head>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>

<body>
<div id="main">
    <div id="bigText">
        <br>
        <table border="1">
            <tr>
                <th>Id</th>
                <th><fmt:message key="registration.login"/></th>
                <th><fmt:message key="table.role"/></th>
                <th><fmt:message key="registration.name"/></th>
                <th><fmt:message key="registration.surname"/></th>
                <th><fmt:message key="registration.address"/></th>
                <th><fmt:message key="registration.phoneNumber"/></th>
                <th> <fmt:message key="table.ban"/></th>
            </tr>
            <c:forEach items="${sessionScope.users}" var="cetegory">
                <tr>
                    <td><c:out value="${cetegory.getId()}"/></td>
                    <td><c:out value="${cetegory.getLogin()}"/></td>
                    <td><c:out value="${cetegory.getClass().getSimpleName()}"/></td>
                    <c:if test="${cetegory.getClass().getSimpleName() == 'Client'}">
                        <td><c:out value="${cetegory.getName()}"/></td>
                        <td><c:out value="${cetegory.getSurname()}"/></td>
                        <td><c:out value="${cetegory.getAddress()}"/></td>
                        <td><c:out value="${cetegory.getPhoneNumber()}"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${cetegory.isBanned() == 'true'}">
                                    <form>
                                        <input type="checkbox" checked
                                               onClick="location.href='controller?action=baningClient&id=${cetegory.getId()}&value=false'">
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <form>
                                        <input type="checkbox"
                                               onClick="location.href='controller?action=baningClient&id=${cetegory.getId()}&value=true'">
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </c:if>
                </tr>
            </c:forEach>

        </table>
    </div>
</div>

</body>
</html>
