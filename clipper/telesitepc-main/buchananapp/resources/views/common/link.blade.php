<link href="{{ url('assets/images/logo.png') }}" rel="icon">
<!-- Mobile Metas -->
<meta name="csrf-token" content="{{ csrf_token() }}">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />

<!-- Web Fonts  -->
<!-- <link href="https://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700,800|Shadows+Into+Light" rel="stylesheet" type="text/css"> -->

<!-- Vendor CSS -->
<link rel="stylesheet" href="{{ url('assets/vendor/bootstrap/css/bootstrap.css') }}" />

<link rel="stylesheet" href="{{ url('assets/vendor/font-awesome/css/font-awesome.css') }}" />
<link rel="stylesheet" href="{{ url('assets/vendor/magnific-popup/magnific-popup.css') }}" />
<link rel="stylesheet" href="{{ url('assets/vendor/bootstrap-datepicker/css/bootstrap-datepicker3.css') }}" />
@yield('css')
<!-- Specific Page Vendor CSS-->
<link rel="stylesheet" href="{{ url('assets/vendor/select2/css/select2.css') }}" /> 
<link rel="stylesheet" href="{{ url('assets/vendor/select2-bootstrap-theme/select2-bootstrap.css') }}" />

<!-- <link rel="stylesheet" href="{{ url('assets/vendor/jquery-datatables-bs3/assets/css/datatables.css') }}" />
 --><link rel="stylesheet" href="{{ url('assets/vendor/summernote/summernote.css') }}" />
<!-- Thee CSS -->
<link rel="stylesheet" href="{{ url('assets/stylesheets/theme.css') }}" />
<link rel="stylesheet" href="{{ url('assets/vendor/simple-line-icons/css/simple-line-icons.css') }}" />
<!-- Skin CSS -->
<link rel="stylesheet" href="{{ url('assets/stylesheets/skins/default.css') }}" />
<!-- Theme Custom CSS -->
<link rel="stylesheet" href="{{ url('assets/stylesheets/theme-custom.css') }}">
<link rel="stylesheet" href="{{ url('assets/vendor/jquery-datatables/media/css/jquery.dataTables.css') }}" />
<link rel="stylesheet" type="text/css" href="{{ url('assets/css/toastr.css') }}">
<link rel="stylesheet" type="text/css" href="{{ url('assets/css/mycss.css') }}">

<!-- Head Libs -->
<script src="{{ url('assets/vendor/modernizr/modernizr.js') }}"></script>