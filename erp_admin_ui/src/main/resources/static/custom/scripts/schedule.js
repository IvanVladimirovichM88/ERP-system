let _runEffect;
$(function() {
    function runEffect() {
        $("#effect").effect("drop", {}, 500, callback);
    };

    function callback() {
        $("#effect").removeAttr("style").hide().fadeIn();
    };

    _runEffect = function() {
        runEffect();
        return false;
    }
});

(function bindDurationsWithFinishDateTimes() {
    let opEntries = $(".op-entry");
    for (let i = 0; i < opEntries.length; ++i) {
        let durationInput = opEntries[i].getElementsByClassName("duration-input")[0];
        let startDateTimeInput = opEntries[i].getElementsByClassName("start-datetime-input")[0];
        let finishDateTimeInput = opEntries[i].getElementsByClassName("finish-datetime-input")[0];

        const updateFinishDateTimeInput = function() {
            if (!startDateTimeInput.value.isBlank()) {
                let d = new Date(startDateTimeInput.value);
                d.setMinutes(d.getMinutes() + durationInput.value * 60);
                finishDateTimeInput.value = d.toDomString();
                updateTechnologyAndChart();
            }
        }

        durationInput.onchange = updateFinishDateTimeInput;
        startDateTimeInput.onchange = updateFinishDateTimeInput;
    }
})();

String.prototype.isBlank = function() {
    return !(this).trim();
}

Date.prototype.toDomString = function() {
    function pad(number) {
        if (number < 10) {
            return '0' + number;
        }
        return number;
    }
    return this.getFullYear() +
        '-' + pad(this.getMonth() + 1) +
        '-' + pad(this.getDate()) +
        'T' + pad(this.getHours()) +
        ':' + pad(this.getMinutes());
}

let chart, dataTable, options, t;

google.charts.load('current', {
        'packages': ['timeline']
    })
    .then(() => {
        chart = new google.visualization.Timeline(document.getElementById('chart-div'));

        dataTable = new google.visualization.DataTable();

        dataTable.addColumn('string', 'Workcell');
        dataTable.addColumn('string', 'Operation');
        dataTable.addColumn('date', 'Start DateTime');
        dataTable.addColumn('date', 'Finish DateTime');

        $.get(`${applicationUrl}/api/technology/get/${technologyId}`, function(data) {
            t = data;
            t.opEntries.sort((o1, o2) => o1.turn - o2.turn);
            updateTechnologyAndChart();
        });
    });


function updateTechnologyAndChart() {
    dataTable.removeRows(0, dataTable.getNumberOfRows());

    let workcells = $(".workcell-input").map((i, elt) => elt.value).toArray();
    let operations = $(".operation-input").map((i, elt) => elt.value).toArray();
    let durations = $(".duration-input").map((i, elt) => elt.value).toArray();
    let starts = $(".start-datetime-input").map((i, elt) => elt.value).toArray();
    let finishs = $(".finish-datetime-input").map((i, elt) => elt.value).toArray();

    for (let i = 0; i < workcells.length; ++i) {
        let start = starts[i].isBlank() ? null : new Date(starts[i]);
        let finish = finishs[i].isBlank() ? null : new Date(finishs[i]);
        if (!(start && finish)) {
            continue;
        }

        dataTable.addRow([
            workcells[i],
            operations[i],
            start,
            finish,
        ]);

        t.opEntries[i].duration = durations[i];
        t.opEntries[i].startDateTime = starts[i];
        t.opEntries[i].finishDateTime = finishs[i];

    }

    if (dataTable.getNumberOfRows() === 0) {
        return;
    }

    options = {
        height: 50 + dataTable.getNumberOfRows() * 45,
        gantt: {
            trackHeight: 45
        }
    };

    chart.draw(dataTable, options);
}

$(".finish-datetime-input ").change(updateTechnologyAndChart);
$("#button-save").click(
    function() {
        fetch(
            `${applicationUrl}/api/technology/post`, {
                method: "POST",
                body: JSON.stringify(t),
                headers: {
                    'Content-Type': 'application/json'
                }
            }
        ).then(_runEffect);
    }
);