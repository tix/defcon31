<!doctype html>
<html class="fixed dark">
<head>
    <title>Google 2FA</title>
    @include('common.link')

    <style>
        html, body {
            height: 100%;
        }

        body {
            margin: 0;
            padding: 0;
            width: 100%;
            display: table;
            font-family: 'Lato', sans-serif;
            font-weight: 800;
        }

        .container {
            text-align: center;
            display: table-cell;
            vertical-align: middle;
        }

        .content {
            text-align: center;
            display: inline-block;
        }

        .title {
            font-size: 96px;
        }

        .key {
            font-size: 50px;
            font-weight: 800;
            color: blue;
        }

        .qrcode {
            float: right;
            width: 280px;
            margin: 40px 90px 45px 20px;
            padding: 15px;
            border: 1px solid black;
            text-align: center;
        }
    </style>
</head>
<body>
<!-- start: page -->
<section class="body-sign">
    <div class="center-sign">
        <div class="logo pull-left">
            <img src="assets/images/logo.png" height="54" alt="Porto Admin" />
            <span class="logotitle h2"><strong>{{ env('APP_NAME') }}</strong></span>
        </div>

        <div class="panel panel-sign">
            <div class="panel-title-sign mt-xl text-right">
                <h2 class="title text-uppercase text-weight-bold m-none"><i class="fa fa-user mr-xs"></i> Google 2FA</h2>
            </div>
            <div class="panel-body">
                @csrf
                <div>secret key</div>
                <div class="key">{{ $key }}</div>
                <div class="qrcode">
                Google QRCode
                <img src="{{ $googleUrl }}" alt="">
                </div>
                <form action="/google2fa/authenticate" method="post">
                    Type your code: <input type="hidden" name="_token" value="{{ csrf_token() }}">
                    <input type="text" name="code">
                    <input type="submit" value="check">
                </form>

                @if ($valid)
                    <div style="color: green; font-weight: 800;">VALID</div>
                @else
                    <div style="color: red; font-weight: 800;">INVALID</div>
                @endif
            </div>
        </div>

    </div>
</section>
<!-- end: page -->
</body>
</html>
