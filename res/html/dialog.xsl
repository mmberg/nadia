<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
xmlns:n="http://mmberg.net/nadia">

<xsl:output method="html" version="4.0"
encoding="iso-8859-1"/>

<xsl:template match="/">
<html>
 <head>
 <link rel="stylesheet" type="text/css" href="/nadia/dialog.css"/>
 </head>
  <body>
   <xsl:apply-templates/>
  </body>
</html>
</xsl:template>

<xsl:template match="n:dialog">
<div class="dialog">
<h3>Dialog: <xsl:value-of select="@name"/></h3>
   <xsl:apply-templates/>
</div>
</xsl:template>

<xsl:template match="task">
<div class="task">
<h3>Task: <xsl:value-of select="@name"/></h3>
   <xsl:apply-templates/>
</div>
</xsl:template>

<xsl:template match="tasks">
   <xsl:apply-templates/>
</xsl:template>

<xsl:template match="AQD">
   <xsl:apply-templates/>
</xsl:template>

<xsl:template match="type">
   <xsl:apply-templates/>
</xsl:template>

<xsl:template match="form">
   <xsl:apply-templates/>
</xsl:template>

<xsl:template match="context">
   <xsl:apply-templates/>
</xsl:template>

<xsl:template match="itos">
<div class="itos">
   <xsl:apply-templates/>
</div>
</xsl:template>

<xsl:template match="//answerType">
<i><xsl:value-of select ="name(.)"/></i>: <b><xsl:apply-templates/></b><br/>
</xsl:template>

<xsl:template match="ito">
<div class="ito">
<h3>ITO: <xsl:value-of select="@name"/></h3>
   <xsl:apply-templates/>
</div>
</xsl:template>

<xsl:template match="action">
<div class="action">
Action &gt; <xsl:apply-templates/>
</div>
</xsl:template>

<xsl:template match="selector">
<div class="selector">
   Selector &gt; <xsl:apply-templates/>
</div>
</xsl:template>

<xsl:template match="followup">
<div class="followup">
FollowUp: <xsl:apply-templates/>
</div>
</xsl:template>

<xsl:template match="answerMapping">
<div class="answerMapping">
AnswerMapping:<br/>
<ul><xsl:apply-templates/></ul>
</div>
</xsl:template>

<xsl:template match='action/*'>
<xsl:value-of select ="name(.)"/><br/>
<ul>
   <xsl:apply-templates/>
</ul>
</xsl:template>

<xsl:template match='action/*/*'>
   <li><i><xsl:value-of select ="name(.)"/></i>: <xsl:apply-templates/></li>
</xsl:template>

<xsl:template match='selector/*'>
  <span><xsl:value-of select ="name(.)"/></span><br/>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match='selector/*/*'>
   <span><xsl:apply-templates/>&#160;&#160;</span>
</xsl:template>

<xsl:template match='answerMapping/item'>
   <li><xsl:value-of select ="@key"/>: <xsl:apply-templates/></li>
</xsl:template>

<xsl:template match='action/*/resultMappings'>
   <li><i><xsl:value-of select ="name(.)"/></i>:
   <ul><xsl:apply-templates/></ul>
   </li>
</xsl:template>

<xsl:template match='action/*/resultMappings/resultMapping'>
   <li><xsl:apply-templates/></li>
</xsl:template>

<xsl:template match='*'>
   <span><i><xsl:value-of select ="name(.)"/></i>: <xsl:apply-templates/>&#160;&#160;</span>
</xsl:template>

</xsl:stylesheet>