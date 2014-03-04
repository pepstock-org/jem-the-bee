<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:info="xalan://org.apache.xalan.lib.NodeInfo"
	xmlns:exception="org.pepstock.jem.ant.validator.transformer.ValidationExceptionThrower"
	extension-element-prefixes="exception">

	<xsl:template name="validate" match="/">

		<xsl:for-each select="/project/property[@name='jem.job.environment']">
			<xsl:choose>
				<xsl:when test="@value">
					<xsl:if test="@value=''">
						<!-- throw validate exception -->
						<xsl:value-of select="exception:throwValidateException('jem.job.environment is empty', info:lineNumber( ), info:columnNumber( ))" />
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="exception:throwValidateException('jem.job.environment not set', info:lineNumber( ), info:columnNumber( ))" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>