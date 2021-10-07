
function getAllRows() {
    $.ajax({
        url: '/customergroup/getAllCustomerGroup',
        type: 'GET',
        contentType: "application/json",
        dataType: 'json',
        success: function (data) {
            if (data != null) {
                rows(data);
            }
        },
        error: function (err) {
            console.log(err)
        }
    })
}
getAllRows();


function rows(data) {
    console.log(data)
    let html = ``
    if(data.length<1){
        $('#error_message').show();
        $('#group_control').hide();
    }
    for (let i = 0; i < data.length; i++) {
        const item = data[i];
        html += `<tr role="row" class="odd">
            <td>`+item.cu_gr_id+`</td>
            <td>`+item.cu_gr_name+`</td>
            <td class="text-right" >
              <div class="btn-group" role="group" aria-label="basic mixed styles example">
                <button onclick="deleteCustomerGroup(`+item.cu_gr_id+`)" type="button" class="btn btn-outline-primary "><i class="far fa-trash-alt"></i></button>
                <button onclick="updateCustomerGroup(`+item.cu_gr_id+`)" type="button" class="btn btn-outline-primary "><i class="far fa-edit"></i></button>
               </div>
            </td>
          </tr>`;
    }
    if(data.length>0){
        $('#error_message').hide();
        $('#group_control').show();
        $('#tableRows').html(html);
    }

}

function deleteCustomerGroup(index){
    if (confirm("Silmek istediğinizden emin misiniz?")) {
        $.ajax({
            url: '/customergroup/delete/' + index,
            type: 'GET',
            contentType: "application/json",
            dataType: 'json',
            success: function (data) {
                if (data == true) {
                    getAllRows();
                } else {
                    alert("Silinmek istenen bilgi mevcut değil")
                }
            },
            error: function (err) {
                console.log(err)
            }
        })
    }
}

function updateCustomerGroup(index){
    $.ajax({
        url: '/customergroup/update/' + index,
        type: 'GET',
        contentType: "application/json",
        dataType: 'json',
        success: function (data) {
            if (data[0] == true) {
                $('#cu_gr_name').val(data[1].cu_gr_name);
                $('#cs_group2').click();
            } else {
                alert("Düzenlenmek istenen alan mevcut değil")
            }
        },
        error: function (err) {
            console.log(err)
        }
    })
}
let process;

$("#cs_group2").click(function() {
    if(proControl != null){
        processType(2)
        process = 2;
    }else{
        processType(3)
        process = 3;
    }
});

$("#cs_group").click(function() {
    if(proControl != null){
        $('#cu_gr_name').val("");
        processType(1)
        process = 1;
    }else{
        processType(3)
        process = 3;
    }
});

function processType(processType){
    $.ajax({
        url: '/customergroup/processtype/' + processType,
        type: 'GET',
        contentType: "application/json",
        dataType: 'json',
        success: function (data) {
        },
        error: function (err) {
        }
    })
}

let proControl = true;
if ($('#cs_group').val() == "true") {
    console.log("Validation'a takıldı.")
    console.log("Form yükleniyor...")
    console.log(process)
    if(process == null){
        proControl = process;
    }

    if(process == 1){
        $('#cs_group').click();
    }else{
        $('#cs_group2').click();
    }

}


/* custoemrGroup search start*/
$(document).on('keyup', '#csgroup_search', function (e) {
    let strSearch = $("#csgroup_search").val();
    console.log(strSearch);
    if (strSearch.trim() != "") {
        getAllSearchList(strSearch);
    } else {
        getAllRows();
    }
})

function getAllSearchList(strSearch) {
    $.ajax({
        url: '/customergroup/search/' + strSearch,
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
/* customerGroup search finish*/