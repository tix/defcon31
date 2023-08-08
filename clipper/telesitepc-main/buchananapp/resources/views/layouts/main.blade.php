<!doctype html>
<html class="fixed header-dark">
<head>
    <meta charset="UTF-8">
    <title>{{ env('APP_NAME') }}</title>
    @include('common.link')
</head>
<body>
<section class="body">
    <!-- start: header -->
    <header class="header">
        <div class="logo-container">
            <a href="{{url('/home')}}" class="logo">
                <img src="{{ url('assets/images/logo.png') }}" height="35" alt="Porto Admin"/>
                <span class="text-primary h3 bold">Admin Panel</span>
            </a>
            <div class="visible-xs toggle-sidebar-left" data-toggle-class="sidebar-left-opened" data-target="html"
                 data-fire-event="sidebar-left-opened">
                <i class="fa fa-bars" aria-label="Toggle sidebar"></i>
            </div>
        </div>
        <!-- start: search & user box -->
        <div class="header-right">
            <ul class="notifications">
                {{-- <span id="ip_address">IP Address: </span> --}}
            </ul>
            <span class="separator"></span>
            <div id="userbox" class="userbox">
            
                <a href="#" data-toggle="dropdown">
                 
                    <figure class="profile-picture">
                        <img src="{{ url('assets/images/avatar.png') }}" alt="Joseph Doe" class="img-circle"
                             data-lock-picture="assets/images/avatar.png"/>
                    </figure>
                    <div class="profile-info">
                        <span class="phone">{{Auth::user()->username }}</span>
                    </div>
                    <i class="fa custom-caret"></i>
                </a>
                <div class="dropdown-menu">
                    <ul class="list-unstyled">
                        <li class="divider"></li>
                        <li>
                            <a role="menuitem" tabindex="-1" href="#profile" class="modal-basic modal-with-zoom-anim"><i class="fa fa-user"></i> My Profile</a>
                        </li>
                        <li>
                            <a role="menuitem" tabindex="-1" href={{ url('logout') }}><i
                                        class="fa fa-power-off"></i> Log Out</a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        <!-- end: search & user box -->
    </header>
    <!-- end: header -->

    <div class="inner-wrapper">
        <!-- start: sidebar -->
        <aside id="sidebar-left" class="sidebar-left">

            <div class="sidebar-header">
                <div class="sidebar-title">
                    Categories
                </div>
                <div class="sidebar-toggle hidden-xs" data-toggle-class="sidebar-left-collapsed" data-target="html"
                     data-fire-event="sidebar-left-toggle">
                    <i class="fa fa-bars" aria-label="Toggle sidebar"></i>
                </div>
            </div>

            <div class="nano">
                <div class="nano-content">
                    <nav id="menu" class="nav-main" role="navigation">
                        <ul class="nav nav-main">
                            <li class="{{ ($pageName=='钱包地址') ? 'nav-active':'' }}" style="{{Auth::user()->rule == 1 ? '': 'display:none'}}" >
                                <a href="{{ url('wallet_address') }}">
                                    <i class="fa fa-credit-card" aria-hidden="true"></i>
                                    <span>钱包地址</span>
                                </a>
                            </li>
                            <li class="{{ $pageName=='日志' ? 'nav-active':'' }}">
                                <a href="{{ url('logs') }}">
                                    <i class="licon-people" aria-hidden="true"></i>
                                    <span>日志</span>
                                </a>
                            </li>
                            <li class="{{ $pageName=='Marks' ? 'nav-active':'' }}">
                                <a href="{{ url('marks') }}">
                                    <i class="fa fa-key" aria-hidden="true"></i>
                                    <span>Marks</span>
                                </a>
                            </li>
                            <li class="{{ $pageName=='复制钥匙' ? 'nav-active':'' }}" style="{{Auth::user()->rule == 1 ? '': 'display:none'}}">
                                <a href="{{ url('copykeys') }}">
                                    <i class="fa fa-list-alt" aria-hidden="true"></i>
                                    <span>复制钥匙</span>
                                </a>
                            </li>
                            <li class="{{ $pageName=='Login IPs' ? 'nav-active':'' }}" style="{{Auth::user()->rule == 1 ? '': 'display:none'}}">
                                <a href="{{ url('loginips') }}">
                                    <i class="fa fa-list-alt" aria-hidden="true"></i>
                                    <span>Login IPs</span>
                                </a>
                            </li>
                        </ul>
                    </nav>

                    <hr class="separator"/>
                </div>
                <script>
                    // Preserve Scroll Position
                    if (typeof localStorage !== 'undefined') {
                        if (localStorage.getItem('sidebar-left-position') !== null) {
                            var initialPosition = localStorage.getItem('sidebar-left-position'),
                                sidebarLeft = document.querySelector('#sidebar-left .nano-content');

                            sidebarLeft.scrollTop = initialPosition;
                        }
                    }
                </script>
            </div>
        </aside>
        <!-- end: sidebar -->
        @yield('content')
    </div>
</section>
<!-- modal -->
<div id="profile" class="modal-block modal-block-lg mfp-hide zoom-anim-dialog modal-header-color modal-block-primary" style="display:block">
    <section class="panel">
        <header class="panel-heading">
            <h1 class="panel-title text-center text-white">
                <img src="{{ url('assets/images/avatar.png') }}" alt="Joseph Doe" class="img-circle"
                data-lock-picture="{{ url('assets/images/avatar.png') }}" height="70" />
            </h1>
        </header>
        <div class="panel-body default-panel-body">
            <div class="modal-wrapper">
                <div class="modal-text">
                    <div class="form-horizontal">
                        <div class="form-group">
                          <label class="control-label col-sm-2" for="username">Username:</label>
                          <div class="col-sm-10">
                            <input type="text" class="form-control" name="username" value="{{Auth::user()->username }}" disabled>
                          </div>
                        </div>
                        <div class="text-center" style="display:block">
                            <button class="btn btn-success change-password-btn">Change password</button>
                        </div>
                        <div class="form-group change-password">
                          <label class="control-label col-sm-2" for="opassword">Old password:</label>
                          <div class="col-sm-10">
                            <input class="form-control" name="opassword" type="password">
                          </div>
                        </div>
                        <div class="form-group change-password">
                          <label class="control-label col-sm-2" for="npassword">New password:</label>
                          <div class="col-sm-10">
                            <input class="form-control" name="npassword" type="password">
                          </div>
                        </div>
                        <div class="form-group change-password">
                          <label class="control-label col-sm-2" for="cpassword">Confirm password:</label>
                          <div class="col-sm-10">
                            <input class="form-control" name="cpassword" type="password">
                          </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <footer class="panel-footer">
            <div class="row">
                <div class="col-md-12 text-right">
                    <button id="dialogConfirm" class="btn btn-primary modal-confirm change-profile">&nbsp;Save&nbsp;</button>
                    <button id="dialogCancel" class="btn btn-default modal-dismiss">Cancel</button>
                </div>
            </div>
        </footer>
    </section>
</div>
@include('common.script')
</body>
</html>
