<?php include 'quizzyConfig.php'; ?>
<script type="text/javascript">
  quizWidth = <?php echo $quizWidth; ?>;
  quizHeight = <?php echo $quizHeight; ?>;
</script>
<div id="quizzy" style="width: <?php echo $quizWidth; ?>px; height: <?php echo $quizHeight; ?>px">
	<div id="quizzy_c" style="width: <?php echo ($quizWidth * 3); ?>px">
		<div id="quizzy_load" style="width: <?php echo $quizWidth; ?>px"></div>
		<div id="quizzy_quiz" style="width: <?php echo ($quizWidth * 3); ?>px"></div>
	</div>
</div>
