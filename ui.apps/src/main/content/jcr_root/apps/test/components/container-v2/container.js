"use strict";
use(function () {
    var apidata;
    $.ajax('https://fakestoreapi.com/products',
        {
            async : false,
            success: function (data, status, xhr) {
                console.log(data);
                apidata = data;
            }
        });
    return apidata;
});