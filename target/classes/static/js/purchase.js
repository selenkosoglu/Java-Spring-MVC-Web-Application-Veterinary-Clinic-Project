$(document).on('click', '#modalButton', function (e) {
    console.log("modal button tıklandı mı???")


//SUPPLIER
    $.ajax({
        url: 'http://localhost:8090/purchase/getSupplierList',
        type: 'GET',
        contentType: "application/json",
        dataType: 'json',
        success: function (data) {
            if (data != null) {
                console.log(data)
                appendSupplierOptions(data);
            } else {
                alert("Veritabanında tedarikçi mevcut değil.")
            }
        },
        error: function (err) {
            console.log(err)
        }
    })
//PRODUCT
    $.ajax({
        url: 'http://localhost:8090/purchase/getProductList',
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

//supplier
function appendSupplierOptions(data) {
    let html = `<option data-subtext="" value="0">Tedarikçi Seçiniz</option>`;
    for (let i = 0; i < data.length; i++) {
        const item = data[i];
        html += `<option value="` + item.supplier_id + `" data-subtext="` + item.supplier_id + `">` + item.supplier_name + `</option>`;
    }
    $('#sFName').html(html);
    $('#sFName ').selectpicker("refresh");
}

//product
function appendProductOptions(data) {
    let html = `<option data-subtext="" value="0">Ürün Seçiniz</option>`;
    for (let i = 0; i < data.length; i++) {
        const item = data[i];
        html += `<option value="` + item.product_id + `" data-subtext="` + item.product_id + `">` + item.product_name + `</option>`;
    }
    $('#proSearchName').html(html);
    $('#proSearchName').selectpicker("refresh");
}

$("#search").keyup(function () {

    const ara = $("#search").val().trim()
    if (ara != "") {
        getAllRowsAfterSearching(ara);
    } else {
        getAllRows();
    }

})

function getAllRows() {
    $.ajax({
        url: '/purchase/getRows',
        type: 'GET',
        contentType: "application/json",
        dataType: 'json',
        success: function (data) {
            if (data != null) {
                rows(data);
            } else {
                alert("Veritabanında henüz alış geçmişi mevcut değil.")
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
        url: '/purchase/getRowsSearching/' + ara,
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
        $('#purchase_control').hide();
    }
    for (let i = 0; i < data.length; i++) {
        const itm = data[i];
        html += `<tr role="row" class="odd">
            <td>` + itm.date + `</td>
            <td>` + itm.purchase_code + `</td>
            <td>` + itm.supplier + `</td>
            <td>` + itm.product + `</td>
            <td>` + itm.purchase_number + `</td>
            <td>` + itm.purchase_total + `</td>
            <td>` + convertPay(itm.purchase_type) + `</td>
            <td>` + itm.purchase_detail + `</td>
              <td class="text-right" >
              <div class="btn-group" role="group" aria-label="Basic mixed styles example">
                <button onclick="fncPurchaseDelete(` + itm.purchase_id + `)" type="button" class="btn btn-outline-primary "><i class="far fa-trash-alt"></i></button>
               </div>
            </td>
          </tr>`;

    }
    if (data.length > 0) {
        $('#error_message').hide();
        $('#purchase_control').show();
        $('#tablePurchase').html(html);
    }
}

if ($('#modalButton').val() == "true") {
    console.log("Validation'a takıldı.")
    console.log("Form yükleniyor...")
    $('#modalButton').click();
}


$(document).ready(function () {
    $('[id^=purchase_number]').keypress(validateNumber);
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

//48 --> 0 57 --> 9

function fncPurchaseDelete(purchaseId) {
    if (confirm("Silmek istediğinize emin misiniz?")) {
        $.ajax({
            url: '/purchase/delete/' + purchaseId,
            type: 'GET',
            contentType: "application/json",
            dataType: 'json',
            success: function (data) {
                if (data == true) {
                    alert("Silme işlemi başarılı!")
                    $("#search").keyup;

                } else {
                    alert("Silinmeye çalışılan ürün veritabınında bulunamadı.")
                    $("#search").val("");
                    $("#search").keyup;
                }
            },
            error: function (err) {
                console.log(err)
            }
        })
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
