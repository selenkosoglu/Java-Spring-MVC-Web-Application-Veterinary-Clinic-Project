$("#search").keyup(function () {

    const ara = $("#search").val()
    if (ara != "") {
        getAllRowsAfterSearching(ara);
    } else {
        getAllRows();
    }

})

function getAllRows() {
    $.ajax({
        url: '/lab/labList',
        type: 'GET',
        contentType: "application/json",
        dataType: 'json',
        success: function (data) {
            if (data != null) {
                rows(data);
            } else {
                alert("Veritabanında henüz lab girişi mevcut değil.")
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
        url: '/lab/labList/' + ara,
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
        $('#lab_control').hide();
    }
    for (let i = 0; i < data.length; i++) {
        const itm = data[i];
        html += `<tr role="row" class="odd">
            <td>` + itm.customer + `</td>
            <td>` + itm.doctor + `</td>
            <td>` + convertLabtype(itm.lab_type) + `</td>
            <td>` + itm.lab_detail + `</td>
            <td class="text-right" >
              <div class="btn-group" role="group" aria-label="Basic mixed styles example">
                 <a href="http://localhost:8090/uploads/` + itm.lab_file + `" class="btn btn-outline-primary" style="margin-right: 10px" target="_blank"><i class="far fa-file-alt"></i></a>
                 <button onclick="deleteLabResult(` + itm.lab_id + `)" type="button" class="btn btn-outline-primary "><i class="far fa-trash-alt"></i></button>
               </div>
            </td>
          </tr>`;
    }
    if (data.length > 0) {
        $('#error_message').hide();
        $('#lab_control').show();
        $('#tableLab').html(html);
    }
}

function convertLabtype(vat) {
    if (vat == 1) {
        return "Hemogram";
    } else if (vat == 2) {
        return "Röntgen";
    } else {
        return "Biyokimya";
    }
}

if ($('#modalButton').val() == "true") {
    console.log("Validation'a takıldı.")
    console.log("Form yükleniyor...")
    $('#modalButton').click();
}

function deleteLabResult(i) {
    if (confirm("Silmek istediğinize emin misiniz?")) {
        $.ajax({
            url: '/lab/delete/' + i,
            type: 'GET',
            contentType: "application/json",
            dataType: 'json',
            success: function (data) {
                if (data == true) {
                    $("#search").keyup();
                } else {
                    alert("Silinmeye çalışılan ürün veritabınında bulunamadı.")
                    $("#search").keyup();
                }
            },
            error: function (err) {
                console.log(err)
            }
        })
    }
}


$('#submitButton').click(function () {
    if (!$('#lab_file').val()) {
        alert("File seçilmedi.");
    }
})

