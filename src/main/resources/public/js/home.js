(function () {
    'use strict';

    var navWrap = document.querySelector('.home-navbar-wrap');
    if (navWrap) {
        var onScroll = function () {
            if (window.scrollY > 12) {
                navWrap.classList.add('is-scrolled');
            } else {
                navWrap.classList.remove('is-scrolled');
            }
        };
        onScroll();
        window.addEventListener('scroll', onScroll, { passive: true });
    }

    var toggle = document.querySelector('.home-nav-toggle');
    var collapse = document.querySelector('.home-nav-collapse');
    if (toggle && collapse) {
        toggle.addEventListener('click', function () {
            var open = collapse.classList.toggle('is-open');
            toggle.setAttribute('aria-expanded', open ? 'true' : 'false');
        });
        collapse.querySelectorAll('a').forEach(function (link) {
            link.addEventListener('click', function () {
                collapse.classList.remove('is-open');
                toggle.setAttribute('aria-expanded', 'false');
            });
        });
    }

    var reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (!reduceMotion && 'IntersectionObserver' in window) {
        var els = document.querySelectorAll('.home-reveal');
        var io = new IntersectionObserver(
            function (entries) {
                entries.forEach(function (e) {
                    if (e.isIntersecting) {
                        e.target.classList.add('is-visible');
                        io.unobserve(e.target);
                    }
                });
            },
            { rootMargin: '0px 0px -8% 0px', threshold: 0.12 }
        );
        els.forEach(function (el) {
            io.observe(el);
        });
    } else {
        document.querySelectorAll('.home-reveal').forEach(function (el) {
            el.classList.add('is-visible');
        });
    }
})();
