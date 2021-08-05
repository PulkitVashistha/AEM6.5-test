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
    for (var i = 0; i < data.length; i++) {
        apidata[i].linkURL = JSON.parse(data[i].image);
        apidata[i].title = JSON.parse(data[i].title);
        apidata[i].linkURL = JSON.parse(data[i].image);
        apidata[i].linkURL = JSON.parse(data[i].image);
        apidata[i].linkURL = JSON.parse(data[i].image);
        apidata[i].linkURL = JSON.parse(data[i].image);

    }
    return apidata;
});