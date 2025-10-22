// ОБЩИЙ JS - подключается на ВСЕХ страницах

// Анимация появления при скролле
class ScrollAnimations {
    constructor() {
        this.observer = null;
        this.init();
    }
    
    init() {
        this.setupIntersectionObserver();
        this.observeElements();
    }
    
    setupIntersectionObserver() {
        this.observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('animate-in');
                }
            });
        }, {
            threshold: 0.1,
            rootMargin: '0px 0px -50px 0px'
        });
    }
    
    observeElements() {
        const elements = document.querySelectorAll('.skill-category, .project-card, .contact-item, .bio-text p');
        elements.forEach(el => {
            el.classList.add('animate-on-scroll');
            this.observer.observe(el);
        });
    }
}

// Переключение темы
class ThemeManager {
    constructor() {
        this.themeToggle = null;
        this.init();
    }
    
    init() {
        this.createThemeToggle();
        this.loadSavedTheme();
    }
    
    createThemeToggle() {
        this.themeToggle = document.createElement('button');
        this.themeToggle.innerHTML = '🌓';
        this.themeToggle.className = 'theme-toggle';
        this.themeToggle.title = 'Toggle theme';
        this.themeToggle.addEventListener('click', () => this.toggleTheme());
        document.body.appendChild(this.themeToggle);
        
        // Добавляем стили для кнопки темы
        this.addThemeToggleStyles();
    }
    
    addThemeToggleStyles() {
        const style = document.createElement('style');
        style.textContent = `
            .theme-toggle {
                position: fixed;
                top: 20px;
                right: 20px;
                background: rgba(0, 255, 0, 0.2);
                border: 1px solid #00ff00;
                color: #00ff00;
                padding: 10px;
                border-radius: 50%;
                cursor: pointer;
                z-index: 1001;
                font-size: 1.2em;
                transition: all 0.3s ease;
            }
            
            .theme-toggle:hover {
                background: rgba(0, 255, 0, 0.3);
                transform: scale(1.1);
            }
            
            .light-theme {
                background: linear-gradient(135deg, #f5f5f5 0%, #e0e0e0 100%);
                color: #333;
            }
            
            .light-theme .main-title {
                color: #008800;
                text-shadow: 0 0 10px #008800;
            }
            
            .light-theme .nav-link {
                color: #666;
            }
            
            .light-theme .skill-category,
            .light-theme .project-card,
            .light-theme .contact-item {
                background: rgba(0, 0, 0, 0.05);
                color: #333;
            }
        `;
        document.head.appendChild(style);
    }
    
    toggleTheme() {
        document.body.classList.toggle('light-theme');
        const isLight = document.body.classList.contains('light-theme');
        localStorage.setItem('theme', isLight ? 'light' : 'dark');
    }
    
    loadSavedTheme() {
        if (localStorage.getItem('theme') === 'light') {
            document.body.classList.add('light-theme');
        }
    }
}

// Инициализация когда DOM загружен
document.addEventListener('DOMContentLoaded', function() {
    new ScrollAnimations();
    new ThemeManager();
    
    // Добавляем текущий год в футер
    const footer = document.querySelector('.footer');
    if (footer) {
        const currentYear = new Date().getFullYear();
        footer.innerHTML = footer.innerHTML.replace('2025', currentYear);
    }
});