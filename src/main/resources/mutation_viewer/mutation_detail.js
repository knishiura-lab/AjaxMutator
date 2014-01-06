window.MutationDetailViewer = (function () {
    function MutationDeteilViewer() {
    }
    MutationDeteilViewer.prototype.setTextById = function (content, id) {
        document.getElementById(id).innerText = content;
        return this;
    };

    MutationDeteilViewer.prototype.setFileName = function (fileName) {
        return this.setTextById(fileName, "fileName");
    };

    MutationDeteilViewer.prototype.setContentBeforeMutation = function (content) {
        return this.setTextById(content, "originalFileHead");
    };

    MutationDeteilViewer.prototype.setLines = function (lines) {
        return this.setTextById("line " + lines, "modifiedLines");
    };

    MutationDeteilViewer.prototype.setOriginal = function (original) {
        return this.setTextById(original, "originalContent");
    };

    MutationDeteilViewer.prototype.setMutated = function (mutated) {
        return this.setTextById(mutated, "mutatedContent");
    };

    MutationDeteilViewer.prototype.setContentAfterMutation = function (content) {
        return this.setTextById(content, "originalFileFoot");
    };
    return MutationDeteilViewer;
})();


