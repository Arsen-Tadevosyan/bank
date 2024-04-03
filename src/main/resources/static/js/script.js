function moveToNextInput(input) {
    if (input.value.length === 1) {
    var nextInput = input.nextElementSibling;
    if (nextInput !== null) {
    nextInput.focus();
} else {
    // If there is no next input, simulate TAB key press
    var tabKeyEvent = new KeyboardEvent("keydown", {
    key: "Tab",
    code: "Tab",
    keyCode: 9,
    which: 9,
    shiftKey: false,
    ctrlKey: false,
    altKey: false,
    metaKey: false
});
    input.dispatchEvent(tabKeyEvent);
}
}
}