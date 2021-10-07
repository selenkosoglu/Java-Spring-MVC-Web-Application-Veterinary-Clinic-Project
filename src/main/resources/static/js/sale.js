let productId;
$('#pName').change(function () {
    productId = $("#pName option:selected").attr("value");
    maxAmount();
})

function maxAmount() {
    $.ajax({
        url: 'http://localhost:8090/sale/getAmount/' + productId,
        type: 'GET',
        contentType: "application/json",
        dataType: 'json',
        success: function (data) {
            if (data > 0) {
                maxAmountContinue(data);
            } else {
                alert("Seçilen ürüne ait stok bulunamadı.")
                maxAmountContinue(0);
            }
        },
        error: function (err) {
            console.log(err)
        }
    })
}

let maxNumber = 0;

function maxAmountContinue(data) {
    maxNumber = data;
    $("#pAmount").attr('max', data);
    $("#pAmount").val(data);
}

$(document).on('keyup', '#pAmount', function (e) {
    const value = $("#pAmount").val();
    if (value > maxNumber) {
        $("#pAmount").val(maxNumber);
    }
})


$(document).ready(function () {
    $('[id^=pAmount]').keypress(validateNumber);
});

function validateNumber(event) {
    var key = window.event ? event.keyCode : event.which;
    if (event.keyCode === 8 || event.keyCode === 46) {
        return true;
    } else if (key < 48 || key > 57) {
        return false;
    } else {
        return true;
    }
};


$('#modalButton').click(function () {

//CUSTOMER
    $.ajax({
        url: 'http://localhost:8090/sale/getCustomerList',
        type: 'GET',
        contentType: "application/json",
        dataType: 'json',
        success: function (data) {
            if (data != null) {
                console.log(data)
                appendCustomerOptions(data);
            } else {
                alert("Veritabanında müşteri mevcut değil.")
            }
        },
        error: function (err) {
            console.log(err)
        }
    })
//PRODUCT
    $.ajax({
        url: 'http://localhost:8090/sale/getProductList',
        type: 'GET',
        contentType: "application/json",
        dataType: 'json',
        success: function (data) {
            if (data != null) {
                console.log(data)
                appendProductOptions(data);
            } else {
                alert("Veritabanında ürün mevcut değil.")
            }
        },
        error: function (err) {
            console.log(err)
        }
    })

})

//cName
function appendCustomerOptions(data) {
    let html = `<option data-subtext="" value="0">Hasta Seçiniz</option>`;
    for (let i = 0; i < data.length; i++) {
        const item = data[i];
        html += `<option value="` + item.cu_id + `" data-subtext="` + item.cu_id + `">` + item.cu_name + `</option>`;
    }
    $('#cName').html(html);
    $('#cName').selectpicker("refresh");
}

function appendProductOptions(data) {
    let html = `<option data-subtext="" value="0">Ürün Seçiniz</option>`;
    for (let i = 0; i < data.length; i++) {
        const item = data[i];
        html += `<option value="` + item.product_id + `" data-subtext="` + item.product_id + `">` + item.product_name + `</option>`;
    }
    $('#pName').html(html);
    $('#pName').selectpicker("refresh");
}


$("#search").keyup(function () {

    const ara = $("#search").val().trim();
    if (ara != "") {
        getAllRowsAfterSearching(ara);
    } else {
        getAllRows();
    }

})

function getAllRows() {
    $.ajax({
        url: '/sale/getRows',
        type: 'GET',
        contentType: "application/json",
        dataType: 'json',
        success: function (data) {
            if (data != null) {
                rows(data);
            } else {
                alert("Veritabanında henüz satış geçmişi mevcut değil.")
                rows(null);
            }
        },
        error: function (err) {
            console.log(err)
        }
    })
}

getAllRows();

function getAllRowsAfterSearching(ara) {
    $.ajax({
        url: '/sale/getRowsSearching/' + ara,
        type: 'GET',
        contentType: "application/json",
        dataType: 'json',
        success: function (data) {
            if (data != null) {
                rows(data);
            } else {
                alert("Arama sonucu boş.")
                rows(null);
            }
        },
        error: function (err) {
            console.log(err)
        }
    })
}

function rows(data) {
    console.log(data)
    let html = ``
    if (data.length < 1) {
        $('#error_message').show();
        $('#sale_control').hide();
    }
    for (let i = 0; i < data.length; i++) {
        const itm = data[i];
        html += `<tr role="row" class="odd">
            <td>` + itm.saledate + `</td>
            <td>` + itm.salecode + `</td>
            <td>` + itm.customername + `</td>
            <td>` + itm.productname + `</td>
            <td>` + itm.salenumber + `</td>
            <td>` + convertPay(itm.saletype) + `</td>
            <td>` + itm.saletotal + `</td>
            <td>` + itm.saledetail + `</td>
            <td class="text-right" >
              <div class="btn-group" role="group" aria-label="Basic mixed styles example">
                <button onclick="fncSaleDelete(` + itm.saleId + `)" type="button" class="btn btn-outline-primary "><i class="far fa-trash-alt"></i></button>
               </div>
            </td>
          </tr>`;

    }
    if (data.length > 0) {
        $('#error_message').hide();
        $('#sale_control').show();
        $('#tableSale').html(html);
    }

}

function convertPay(vat) {
    if (vat == 1) {
        return "Nakit";
    } else if (vat == 2) {
        return "Kredi Kartı";
    } else {
        return "Havale";
    }
}

function fncSaleDelete(saleId) {
    if (confirm("Silmek istediğinize emin misiniz?")) {
        $.ajax({
            url: '/sale/delete/' + saleId,
            type: 'GET',
            contentType: "application/json",
            dataType: 'json',
            success: function (data) {
                if (data == true) {
                    alert("Silindi.");
                    maxAmountContinue(0);
                    $("#search").keyup();

                } else {
                    $("#search").val("");
                    $("#search").keyup;
                    alert("Silinmeye çalışılan ürün veritabınında bulunamadı.")
                }
            },
            error: function (err) {
                console.log(err)
            }
        })
    }
}

if ($('#modalButton').val() == "true") {
    console.log("Validation'a takıldı.")
    console.log("Form yükleniyor...")
    $('#modalButton').click();
}