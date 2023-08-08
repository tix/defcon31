@extends('layouts.main')
@section('content')
<section role="main" class="content-body">
    <header class="page-header">
        <h2>{{ ucfirst($pageName) }}</h2>
    </header>
    <!--start content  -->
<!--end content  -->
</section>
@endsection
@section('script')
<script type="text/javascript" src="{{ asset('assets/js/home.js') }}"></script>
@endsection
