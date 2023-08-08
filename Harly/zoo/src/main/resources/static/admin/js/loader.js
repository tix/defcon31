$(document).ajaxSend(function (event, request, settings) {
    $("#wrap-main-content").hide();
    $('#loading-indicator').show();
});

$(document).ajaxComplete(function (event, request, settings) {
    $('#loading-indicator').hide();
    $("#wrap-main-content").show();
});


function close_modal_and_refresh(modal_id, table_id) {
    $(modal_id).modal('hide');
    $(table_id).DataTable().ajax.reload()
}


function reload_table(table_id) {
    $(table_id).DataTable().ajax.reload()
}

function parse_ajax_resp_for_dt(json) {
    var code = json["code"];
    if (code) {
        BootstrapDialog.show({
            type: 'type-' + data["level"],
            closable: true,
            title: data["title"],
            message: data["payload"]
        });
        return [];
    }
    return json['payload'];
}

function build_a_tag_ansyn(title, href, post_action) {
    return '<a class="btn btn-sm" data-sync-action="post" data-href="' + href + '" data-complete-action="' + post_action + '">' + title + '</a>';
}

function build_a_tag_ansyn_modal(title, href, modal_id) {
    var modal_id = modal_id || "#wrap-modal";
    return '<a class="btn btn-sm" data-sync-action="get" data-display-style="modal" data-show-target="' + modal_id + '" data-href="' + href + '">' + title + '</a>';
}

function post_loader(url, param, post_action) {
    if (!param) {
        param = {}
    }
    if ($.type(param) === "object" && !("csrfmiddlewaretoken" in param)) {
        param.csrfmiddlewaretoken = $("#_post_fade_saft").find('input[name="csrfmiddlewaretoken"]').val();
    }
    $.post(url, param, function (data) {
        if (data["code"] == 0 && post_action) {
            eval(post_action)
        } else {
            console.log("error", data)
        }
    }).fail(function (p1, p2, p3) {
        console.log(p1, p2, p3)
    })
}

function post_form(form_id, post_action) {
    if (!form_id) {
        console.log("post form target not set");
        return;
    }
    var param = $(form_id).serialize();
    $.post(url, param, function (data) {
        if (data["code"] == 0 && post_action) {
            eval(post_action)
        } else {
            console.log("error", data)
        }
    }).fail(function (p1, p2, p3) {
        console.log(p1, p2, p3)
    })
}

function init_validator() {
    if (typeof (validator) === 'undefined') {
        return;
    }

    // initialize the validator function
    validator.message.date = 'not a real date';
    validator.message.empty = '不能为空或格式错误';
    validator.message.url = "URL非法";
    validator.defaults.classes.alert = 'col-md-2 alert';

    // validate a field on "blur" event, a 'select' on 'change' event & a '.reuired' classed multifield on 'keyup':
    $('form')
        .on('blur', 'input[required], input.optional, select.required', validator.checkField)
        .on('change', 'select.required', validator.checkField)
        .on('keypress', 'input[required][pattern]', validator.keypress);

    $('.multi.required').on('keyup blur', 'input', function () {
        validator.checkField.apply($(this).siblings().last()[0]);
    });

    $('form').submit(function (e) {
        e.preventDefault();
        var submit = true;

        // evaluate the form using generic validaing
        if (!validator.checkAll($(this))) {
            submit = false;
        }

        if (submit)
            this.submit();

        return false;
    });

};

$(document).ready(function (e) {
    init_validator();

    $(document).on("click", 'a[data-sync-action="general"]', function (e) {
        e.preventDefault();
        var target = $(this).data("show-target") || "#wrap-modal";

        if (show_method == "modal") {
            $(target).html(template(data)).modal('show');
        } else {
            $(target).html(template(data));
        }
    });

    $(document).on("click", 'a[data-sync-action="get"]', function (e) {
        e.preventDefault();
        var url = $(this).data("href");
        var target = $(this).data("show-target") || "#wrap-modal";
        var template_id = $(this).data('hadbars-template');
        $.get(url, function (data) {
            var show_method = $(this).data('display-style') || "modal";
            var template = Handlebars.compile($("#" + template_id).html());
            if (show_method == "modal") {
                $(target).html(template(data)).modal('show');
            } else {
                $(target).html(template(data));
            }
        }).fail(function (p1, p2, p3) {
            console.log(p1, p2, p3)
        })
    });

    $(document).on("click", 'a[data-sync-action="post"]', function (e) {
        e.preventDefault();
        var $form = $($(this).data("param-form") || "#_post_fade_saft");
        if (!validator.checkAll($form)) {
            console.log("check form input failed");
            return;
        }
        var url = $form.attr('action') || $(this).data('href');
        var param = $form.serialize();
        var post_action = $(this).data("complete-action");
        post_loader(url, param, post_action)
    });

    $(document).off('click', ".modal button.close-modal").on("click", ".modal button.close-modal", function (e) {
        e.preventDefault();
        $(this).closest('.modal').modal('hide');
    });

    $(document).off("click", 'a[data-sync-action="reload_table"]').on("click", 'a[data-sync-action="reload_table"]', function (e) {
        e.preventDefault();
        tid = $(this).data("reload-target");
        reload_table(tid);
    })

    // $(window).on('hashchange', function () {
    //     console.log("hash changed")
    //     if (location.hash.length > 1) {
    //         var elements = window.location.hash.split("#");
    //         var targetUrl = elements[1];
    //         var $target = $("#" + elements[2]);
    //         if (!$target.isEmptyObject) {
    //             load($target, targetUrl);
    //         }
    //     }
    // });
})