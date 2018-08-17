<!DOCTYPE html>
<html lang="en">
<head>
	<title>Volunteer Schedules</title>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
	<link rel="stylesheet"
	      href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css"
	      integrity="sha384-WskhaSGFgHYWDcbwN70/dfYBj47jz9qbsMId/iRN3ewGhXQFZCSftd1LZCfmhktB"
	      crossorigin="anonymous">

	<link rel="stylesheet" href="style.css"/>
</head>
<body>
<div class="header">
	<img width="400" src="logo_color.png"/>
	<h1>Volunteer Schedules</h1>
</div>
<div class="container-fluid">
	<#--<div class="row">
		<div class="col-md-12">
		</div>
	</div>-->


	<div style="padding-bottom: 2em;"></div>


	<div class="row">
		<div class="col-md-2">
			<h2>This Week</h2>
		</div>
		<div class="col-md-2">
			<h2><small>${data.thisWeekDate?string["MMMM d"]}</small></h2>
		</div>

		<div class="col-md-2">
			<h2>Next Week</h2>
		</div>
		<div class="col-md-2">
			<h2><small>${data.nextWeekDate?string["MMMM d"]}</small></h2>
		</div>

		<div class="col-md-2">
			<h2>Two Weeks</h2>
		</div>
		<div class="col-md-2">
			<h2><small>${data.twoWeeksDate?string["MMMM d"]}</small></h2>
		</div>
	</div>


	<div style="padding-bottom: 2em;"></div>

	<#--<#list-->

	<div class="row">
		<div class="col-md-12">
			<h3>${data.thisWeekServices?first.name}</h3>
		</div>

		<div class="col-md-4">
			<table class="table">
				<tbody class="table-striped">
					<tr>
						<th scope="row">Coffee Serving</th>
						<td>Janelle Sander</td>
					</tr>
					<tr>
						<th scope="row">Element Prep</td>
						<td class="warning">Alan Balch</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="col-md-4">
			<table class="table">
				<tbody class="table-striped">
					<tr>
						<th scope="row">Coffee Serving</th>
						<td>Janelle Sander</td>
					</tr>
					<tr>
						<th scope="row">Element Prep</td>
						<td class="warning">Alan Balch</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>

	<div class="row">
	</div>
</div>
</body>
</html>