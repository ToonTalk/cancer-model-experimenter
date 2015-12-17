<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<!-- based upon http://www.inf.uni-konstanz.de/gk/pubsys/publishedFiles/BrLePi05.pdf -->
	<xsl:output method="xml" indent="yes" encoding="iso-8859-1" />
	<xsl:strip-space elements="*" />
	<xsl:template match="gxl">
		<xsl:comment>
			This GraphML document was generated from GXL by
			a GXL-to-GraphML conversion style sheet.
		</xsl:comment>
		<graphml>
			<key id="MY-ID" for="node" attr.name="MY-ID" attr.type="string" />
			<key id="KIND" for="node" attr.name="KIND" attr.type="string" />
			<key id="BREED" for="node" attr.name="BREED" attr.type="string" />
			<key id="MY-RULE" for="node" attr.name="MY-RULE" attr.type="string" />
			<key id="MY-VALUE" for="node" attr.name="MY-VALUE" attr.type="string" />
			<key id="MY-SHAPE" for="node" attr.name="MY-SHAPE" attr.type="string" />
			<key id="PROBABILITY-OF-LINK" for="edge" attr.name="PROBABILITY-OF-LINK" attr.type="string" />
			<key id="SIGN-OF-LINK" for="edge" attr.name="SIGN-OF-LINK" attr.type="string" />
			<key id="BREED" for="edge" attr.name="BREED" attr.type="string" />
			<xsl:apply-templates />
		</graphml>
	</xsl:template>
	<xsl:template match="graph">
		<graph id="{@id}">
			<xsl:attribute name="edgedefault">
<xsl:if test="contains(@edgemode,'un')">un</xsl:if>
<xsl:text>directed</xsl:text>
</xsl:attribute>
			<!-- generate a key (at the first appearance of an attr-name) -->
			<xsl:for-each
				select=".//attr[not(@name=../preceding-sibling::*/attr/@name)]">
				<key id="{@name}">
					<xsl:variable name="name" select="@name" />
					<xsl:attribute name="for"> <!-- determine "minimal" domain -->
<xsl:choose>
<xsl:when test="not(@name=../../*[name()!='node']/attr/@name)">node</xsl:when>
<xsl:when test="not(@name=../../*[name()!='edge']/attr/@name)">edge</xsl:when>
<xsl:when test="not(@name=../../*[name()!='rel']/attr/@name)">hyperedge</xsl:when>
<xsl:when test="not(@name=../../*[name()!='graph']/attr/@name)">graph</xsl:when>
<xsl:when test="not(@name=../../*[name()!='relend']/attr/@name)">endpoint</xsl:when>
<xsl:otherwise>all</xsl:otherwise>
</xsl:choose>
</xsl:attribute>
				</key>
			</xsl:for-each>
			<xsl:apply-templates select="*" />
		</graph>
	</xsl:template>
	
	
	
	
	<xsl:template match="node">
		
		<xsl:variable name="id" select="@id" />

		<node id="{$id}">
			
			<xsl:for-each select="../edge[@from=$id]/@toorder">
				<port name="{-1*number(.)}" />
			</xsl:for-each>
			<xsl:for-each select="../edge[@to=$id]/@fromorder">
				<port name="{.}" />
			</xsl:for-each>
			
			
			<data key="MY-ID"><xsl:value-of select="$id" /></data>
			<data key="BREED">objects</data>
			<data key="KIND">Gene</data>
			
			
			<xsl:for-each select="../node[@id=$id]/value">
				<xsl:variable name="value" select="@val" />
				<data key="MY-VALUE"><xsl:value-of select="$value" /></data>
			</xsl:for-each>	
			
			<xsl:for-each select="../node[@id=$id]/value/exp">
				<xsl:variable name="exp" select="@str" />
			
				<xsl:variable name="exp_rep" select="replace($exp,'\|','or')"/>
				<xsl:variable name="exp_rep2" select="replace($exp_rep,'&amp;','and')"/>
				<xsl:variable name="exp_rep3" select="replace($exp_rep2,'!','not ')"/>				
				<data key="MY-RULE"><xsl:value-of select="$exp_rep3" /></data>
				
			</xsl:for-each>		
			
			
			<xsl:for-each select="../node[@id=$id]/nodevisualsetting/ellipse">
				<data key="MY-SHAPE">ellipse</data>
			</xsl:for-each>
			
			
			<xsl:for-each select="../node[@id=$id]/nodevisualsetting/rect">
				<data key="MY-SHAPE">rect</data>
			</xsl:for-each>
			
		

			<xsl:apply-templates/>
			
		</node>
		
	</xsl:template>
	
	<xsl:template match="edge">
		<edge source="{@from}" target="{@to}" sign="{@sign}">
			
			<xsl:variable name="id" select="@id" />
			
		<!--	change this? 			<xsl:copy-of select="@id" />   !-->			
			
			<xsl:if test="@isdirected">
				<xsl:attribute name="directed">
					<xsl:value-of select="@isdirected" />
				</xsl:attribute>
			</xsl:if>
			
			
			<xsl:if test="@fromorder">
				<xsl:attribute name="sourceport"><xsl:value-of select="-1*number(.)" /></xsl:attribute>
			</xsl:if>
			
			
			<xsl:if test="@toorder">
				<xsl:attribute name="targetport"><xsl:value-of select="." /></xsl:attribute>
			</xsl:if>
			
			
			<data key="SIGN-OF-LINK"><xsl:value-of select="@sign" /></data>
			<data key="BREED">influences</data>
	
<!--
			<xsl:for-each select="../edge[@id = $id]/annotation">
				<xsl:variable name="comment" select="*" />
				<data key="PROBABILITY-OF-LINK"><xsl:value-of select="substring-after($comment,'probability=')" /></data>   
			</xsl:for-each>
			
			
-->			
			
			
			<xsl:apply-templates />
		</edge>
	</xsl:template>
	<!-- process only scalar data -->
	<xsl:template match="attr">
		<xsl:if test="count(*)=1">
			<data key="{@name}">
				<xsl:copy-of select="@id" />
				<xsl:value-of select="*[1]" />
			</data>
		</xsl:if>
	</xsl:template>
	<xsl:template match="rel">
		<hyperedge>
			<xsl:copy-of select="@id" />
			<xsl:apply-templates />
		</hyperedge>
	</xsl:template>
	<xsl:template match="relend">
		<endpoint node="{@target}">
			<xsl:attribute name="type">
<xsl:choose>
<xsl:when test="@direction='in'">in</xsl:when>
<xsl:when test="@direction='out'">out</xsl:when>
<xsl:otherwise>undir</xsl:otherwise>
</xsl:choose>
</xsl:attribute>
			<xsl:apply-templates />
		</endpoint>
	</xsl:template>
	<xsl:template match="locator" />
	<xsl:template match="type" />
	<xsl:template match="seq|tup|bag|set" />
</xsl:stylesheet>
