$(document).ready(function () {

    $('.btn-remove-product').click(function (e) {
        $(this).parent().parent().remove();
        var total = calcSubTotal();
        $('#view-cart-form .sub-total').text(total);
        $('#view-cart-form tfoot .total').text(total);
    });

    $('#view-cart-form input[type=number]').change(function (e) {
        var parent = $(this).parent().parent();
        var newVal = Number(parent.find($('.price strong')).text()) * Number($(this).val());
        parent.find($('#view-cart-form .total strong')).text(newVal);
        var total = calcSubTotal();
        $('#view-cart-form .sub-total').text(total);
        $('#view-cart-form tfoot .total').text(total);
    });

    function calcSubTotal() {
        var total = 0;
        $('#view-cart-form .total strong').each(function (index) {
            total += Number($(this).text());
        })
        return total;
    }

    $('#view-cart-form .pull-right .main-btn').click(function (e) {
        e.preventDefault();
        var carts = [];
        $('#view-cart-form tbody tr').each(function () {
            var productCart = {
                id: $(this).attr('id'),
                quantity: $(this).find($('.qty input[type=number]')).val()
            }
            carts.push(productCart);
        });
        var data = JSON.stringify(carts);
        console.log(data);
        $.ajax({
            url: '/modify_cart',
            type: 'post',
            contentType: "application/json; charset=utf-8",
            data: data,

        }).done(function (result) {
            window.location.href = "http://localhost:8080";
        });
    });

    $('#view-cart-form .pull-right .primary-btn').click(function (e) {
        e.preventDefault();
        var carts = [];
        $('#view-cart-form tbody tr').each(function () {
            var productCart = {
                id: $(this).attr('id'),
                quantity: $(this).find($('.qty input[type=number]')).val()
            }
            carts.push(productCart);
        });
        var data = JSON.stringify(carts);
        console.log(data);
        $.ajax({
            url: '/modify_cart',
            type: 'post',
            contentType: "application/json; charset=utf-8",
            data: data,

        }).done(function (result) {
            if (result === 'success') {
                window.location.href = "http://localhost:8080/shipment_details";
            } else {
                $('.order-summary .pull-right .my-alert').append(
                    '                <div class="alert alert-danger alert-dismissible w-100" role="alert">' +
                    '                    <strong>Error! </strong>' + result +
                    '                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">' +
                    '                        <span aria-hidden="true">&times;</span>' +
                    '                    </button>' +
                    '                </div>'
                );

                // $('.order-summary>.pull-right.alert').get(0).css('top', '20px');
            }
        });
    });


    if ($('.my-panel-product .list-info-price span').length > 0) {
        var panelSub = calcSubTotalPanel();
        var shipFee = Number($('.my-panel-product .list-info-price span').get(1).innerHTML);
        var discount = Number($('.my-panel-product .list-info-price span').get(2).innerHTML);
        $('.my-panel-product .list-info-price span').get(0).innerHTML = (panelSub);

        $('.my-panel-product .total span').text((panelSub + shipFee - discount));
    }

    function calcSubTotalPanel() {
        var total = 0;
        $('.my-panel-product .item').each(function (index) {
            total += Number($('.my-panel-product .item .price')[index].innerHTML) * Number($('.my-panel-product .item .title>strong')[index].innerHTML.substring(1));
        });
        return total;
    }
});