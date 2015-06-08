<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:if test="${c:browser('gecko2') || c:browser('ie9') || c:browser('opera') || c:browser('safari')}">
.z-center, .z-west, .z-west-header {
	background: #141414;
	color: #FFF;
}
.z-west-splitter {
	background: #FFF;
}
</c:if>