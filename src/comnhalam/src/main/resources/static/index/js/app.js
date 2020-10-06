$(document).ready(function () {
    PNotify.prototype.options.styling = "fontawesome";
    PNotify.prototype.options.delay = 2000;
    $('.search-input').keyup(function (e) {
        e.preventDefault();
        var product = $('.search-input').val();
        var tag = $('.search-categories').val();
        $.ajax({
            url: '/search_product?product=' + product + '&tag=' + tag,
            type: 'GET',
            dataType: 'html'
        }).done(function (result) {
            $('#result-search').html(result);
        });
    });

    $.ajax({
        url: '/get_tag',
        type: 'GET',
        dataType: 'json'
    }).done(function (data) {
        $('.category-list li').remove();
        $('#list-links li').remove();
        data.forEach(function (d) {
            $('.search-categories')
                .append('<option value="' + d.name + '">' + d.name + '</option>');

            var link = encodeURI(("/products?tag=" + d.name));

            $('.category-list').append(`<li><a href="${link}">${d.name}</a></li>`);
            $('#list-links').append(`<li><a href="${link}">${d.name}</a></li>`);

            console.log(d.name);
        });
        $('.category-list').append(`<li><a href="/products?tag=all">Xem tất cả</a></li>`);
    });

    var resendEmailSubmit = $('#resend-email-submit');
    var resendEmailInput = $('#resend-email-input');
    resendEmailSubmit.attr('disabled', 'disabled');
    resendEmailInput.keyup(function (e) {
        if ($(this).val().length > 0)
            resendEmailSubmit.removeAttr('disabled');
        else
            resendEmailSubmit.attr('disabled', 'disabled');
    });

    var resendForm = $('#resend-email form');
    resendForm.submit(function (e) {
        e.preventDefault();
        $('#loader img').remove();
        $('#loader').append('<img src="/index/img/spinner.gif">');

        var action = $(this).attr('action');
        var method = $(this).attr('method');
        var form_data = $(this).serialize();

        resendEmailSubmit.attr('disabled', 'disabled');
        resendEmailInput.val("");

        $.ajax({
            url: action,
            type: method,
            data: form_data,
            dataType: 'text'
        }).done(function (result) {
            $('#loader img').remove();
            if (result === 'success') {
                $('#loader').append('<img src="/index/img/mail.gif">');
            } else {
                $("#resend-email").append(
                    '<div class="alert alert-danger alert-dismissible">' +
                    '<button type="button" class="close" data-dismiss="alert">&times;</button>' +
                    '<strong>Error! </strong>' + result +
                    '</div>'
                )
                ;
            }
        });
    });

    $('.add-to-cart').click(function (e) {
        e.preventDefault();
        var id = $(this).attr('product_id');

        var quantitySelect = $('.product-btns .qty-input input[type=number]');
        var quantity = 1;
        if(quantitySelect.length > 0) {
            quantity = quantitySelect.val();
        }

        $.ajax({
            url: '/add_product_to_cart/' + id + '/' + quantity,
            type: 'GET',
            dataType: 'html'
        }).done(function (result) {
            $('.header-cart').html(result);
            new PNotify({
                title: 'Thành công!',
                text: 'Đã thêm sản phẩm thành công',
                type: 'success'
            });
        });
    })

    // resendEmailSubmit.click(function (e) {
    //     e.preventDefault();
    //     $('#loader img').remove();
    //     $('#loader').append('<img src="/index/img/spinner.gif">');
    //     var email = resendEmailInput.val();
    //     resendEmailInput.val("");
    //     $(this).attr('disabled', 'disabled');
    //     $.ajax({
    //         url: '/resendVerification?email=' + email,
    //         type: 'GET',
    //         dataType: 'text'
    //     }).done(function (result) {
    //         $('#loader img').remove();
    //         if (result === 'success') {
    //             $('#loader').append('<img src="/index/img/mail.gif">');
    //         } else {
    //             $("#resend-email").append(
    //                 '<div class="alert alert-danger alert-dismissible">' +
    //                 '<button type="button" class="close" data-dismiss="alert">&times;</button>' +
    //                 '<strong>Error! </strong>' + result +
    //             '</div>'
    //         )
    //             ;
    //         }
    //     });
    // });
});