<script src="{{ asset('assets/vendor/jquery/jquery.js') }}"></script>
<script src="{{ asset('assets/vendor/jquery-browser-mobile/jquery.browser.mobile.js') }}"></script>
<script src="{{ asset('assets/vendor/bootstrap/js/bootstrap.js') }}"></script>
<script src="{{ asset('assets/vendor/nanoscroller/nanoscroller.js') }}"></script>
<script src="{{ asset('assets/vendor/bootstrap-datepicker/js/bootstrap-datepicker.js') }}"></script>
 <script src="{{ asset('assets/vendor/magnific-popup/jquery.magnific-popup.js') }}"></script>
<!--<script src="{{ asset('assets/vendor/jquery-placeholder/jquery-placeholder.js') }}"></script>
 --><!-- Specific Page Vendor -->
<script src="{{ asset('assets/vendor/pnotify/pnotify.custom.js') }}"></script>
<script src="{{ asset('assets/vendor/select2/js/select2.js') }}"></script>
<script src="{{ asset('assets/vendor/nanoscroller/nanoscroller.js') }}"></script>
<script src="{{ asset('assets/vendor/ios7-switch/ios7-switch.js') }}"></script>
<!-- <script src="{{ asset('assets/vendor/jquery-datatables/extras/TableTools/js/dataTables.tableTools.min.js') }}"></script> -->
<!-- <script src="{{ asset('assets/vendor/jquery-datatables-bs3/assets/js/datatables.js') }}"></script> -->
<script src="{{ asset('assets/vendor/summernote/summernote.js') }}"></script>
<script src="{{ asset('assets/vendor/jquery-datatables/media/js/jquery.dataTables.js') }}"></script>
<!-- <script src="{{ asset('assets/vendor/jquery-datatables-bs3/assets/js/datatables.js') }}"></script> -->
<!-- Theme Base, Components and Settings -->
<script src="{{ asset('assets/javascripts/theme.js') }}"></script>

<!-- Theme Custom -->
<script src="{{ asset('assets/javascripts/theme.custom.js') }}"></script>

<!-- Theme Initialization Files -->
<script src="{{ asset('assets/javascripts/theme.init.js') }}"></script>
<script src="{{ asset('assets/javascripts/ui-elements/examples.modals.js') }}"></script>
<!-- Examples -->
<!-- <script src="{{ asset('assets/javascripts/tables/examples.datatables.default.js') }}"></script>
<script src="{{ asset('assets/javascripts/tables/examples.datatables.row.with.details.js') }}"></script>
<script src="{{ asset('assets/javascripts/tables/examples.datatables.tabletools.js') }}"></script> -->
<!-- <script src="{{ asset('assets/dist/inputmask/inputmask.js') }}"></script>
<script src="{{ asset('assets/dist/inputmask/inputmask.Extensions.js') }}"></script>
<script src="{{ asset('assets/dist/inputmask/jquery.inputmask.js') }}"></script> -->
<input type="hidden" value="{{ $pageName }}" id="pageName">
<input type="hidden" value="{{ csrf_token() }}" id="csrf_token">
<input type="hidden" value="{{ session()->has('message') ? session('message') :'' }}" id="message">
<script type="text/javascript" src="{{ asset('assets/js/toastr.js') }}"></script>
<script type="text/javascript" src="{{ asset('assets/js/message.js') }}"></script>
<script type="text/javascript" src="{{ asset('assets/js/myjs.js') }}"></script>
<script type="text/javascript" src="{{ asset('assets/js/message_display.js') }}"></script>
<script>
    $(function(){
        // $('#ip_address').html("IP Address: " + localStorage.getItem('ip'));
    });
</script>
@yield('script')
<?php
session()->forget('message');
?>