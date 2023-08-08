var requestJson = function(requestType, url, params, fnCallback) {
    $.ajax({
            type : requestType,
            url : url,
            data : params,
            dataType : 'json',
            success : function(data) {
                    if (data.code==0) {
                            fnCallback(true, data);
                    } else {
                        fnCallback(false, data);
                    }
            },
            error : function() {
                // fnCallback(false, Message.FAILED);
            }
    });
};


