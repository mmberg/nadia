<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html" version="4.0" encoding="iso-8859-1"/>

<xsl:template match="/">
<html>
 <head>
 <link rel="stylesheet" type="text/css" href="/nadia/context.css"/>
 </head>
  <body>
  <h3>Dialog Manager Context:</h3>
   <xsl:apply-templates/>
  </body>
</html>
</xsl:template>

<xsl:template match="dialogManagerContext">
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="dialogHistory">
<span><i><xsl:value-of select ="name(.)"/></i></span>:
<div class="box">
<ul>
<xsl:apply-templates/>
</ul>
</div>
</xsl:template>


<xsl:template match="child[@hasChildren='true']">
<ul><xsl:apply-templates/></ul>
</xsl:template>

<xsl:template match="child[@hasChildren='false']">
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="child/utterance[@type='U']">
<li class="user">U: <xsl:value-of select="."/></li>
</xsl:template>

<xsl:template match="child/utterance[@type='S']">
<li class="system">S: <xsl:value-of select="."/></li>
</xsl:template>

<xsl:template match="taskStack">
<span><i><xsl:value-of select ="name(.)"/></i>:</span>
<div class="box">
<ul>
<xsl:apply-templates/>
</ul>
</div>
</xsl:template>

<xsl:template match="task">
<li>
<xsl:value-of select="."/>
</li>
</xsl:template>

<xsl:template match="frame">
<span><i><xsl:value-of select ="name(.)"/></i>:</span>
<div class="box">
<ul>
<xsl:apply-templates/>
</ul>
</div>
</xsl:template>

<xsl:template match="entry">
<li>
<xsl:value-of select="."/>
</li>
</xsl:template>

<xsl:template match='*'>
   <span><i><xsl:value-of select ="name(.)"/></i>: <xsl:apply-templates/></span><br/>
</xsl:template>

</xsl:stylesheet>