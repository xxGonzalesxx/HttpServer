// ДОПОЛНИТЕЛЬНЫЕ АНИМАЦИИ - только для главной страницы

// Терминал с печатающимся текстом
class Terminal {
    constructor() {
        this.lines = [
            "> Welcome to SGProducts portfolio",
            "> Backend Developer & Problem Solver", 
            "> Specializing in: Java, Kotlin, Spring Boot",
            "> Currently learning: Microservices, Spring Cloud",
            "> Ready for new challenges!"
        ];
        this.init();
    }
    
    init() {
        this.typeLines();
    }
    
    typeLines() {
        let currentLine = 0;
        let currentChar = 0;
        const outputElement = document.getElementById('terminal-output');
        
        if (!outputElement) return; // Если элемента нет, выходим
        
        function type() {
            if (currentLine < this.lines.length) {
                if (currentChar < this.lines[currentLine].length) {
                    outputElement.innerHTML += this.lines[currentLine].charAt(currentChar);
                    currentChar++;
                    setTimeout(() => type.call(this), 50);
                } else {
                    outputElement.innerHTML += '<br>';
                    currentLine++;
                    currentChar = 0;
                    setTimeout(() => type.call(this), 100);
                }
            }
        }
        
        type.call(this);
    }
}

// Инициализация терминала если есть элемент
document.addEventListener('DOMContentLoaded', function() {
    if (document.getElementById('terminal-output')) {
        new Terminal();
    }
});