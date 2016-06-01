var obj = {
 name: "Amy",
 color: "blue"
};

var arr = ["el1", "el2", "el3"];

function submitEntry() {
 var entry1 = document.getElementById("content1");
 var text = document.getElementById("input1").value;
 entry1.innerHTML = text;
}

function changeSize(element) {
 if (element.style.width == "200px") {
  element.style.width = "500px";
 } else {
  element.style.width = "200px";
 }
}

function setColor(element, color) {
 element.style.backgroundColor = color;
}