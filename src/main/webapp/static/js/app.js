/**
 * Lycée Admin - Core Utilities
 */

const LyceeAdmin = {
    /**
     * Change the number of items per page while preserving other query parameters
     * @param {string|number} size 
     */
    changePageSize: function(size) {
        const url = new URL(globalThis.location.href);
        url.searchParams.set('size', size);
        url.searchParams.set('page', '1'); // Reset to page 1
        globalThis.location.href = url.toString();
    },

    /**
     * Navigate to a specific page while preserving other query parameters
     * @param {string|number} page 
     */
    gotoPage: function(page) {
        const url = new URL(globalThis.location.href);
        url.searchParams.set('page', page);
        globalThis.location.href = url.toString();
    },

    /**
     * Initialize event listeners for pagination buttons to avoid inline JS lints
     */
    initPagination: function() {
        document.querySelectorAll('.btn-page[data-page], .btn-page-num[data-page]').forEach(btn => {
            btn.onclick = function() {
                if (this.disabled) return;
                const page = this.dataset.page;
                if (page) LyceeAdmin.gotoPage(page);
            };
        });
    },
    /**
     * Initialize progress bars and charts based on data attributes
     */
    initProgressBars: function() {
        document.querySelectorAll('[data-width]').forEach(el => {
            el.style.width = el.dataset.width;
        });
        document.querySelectorAll('[data-height]').forEach(el => {
            el.style.height = el.dataset.height;
        });
    },
    /**
     * Hide broken images globally
     */
    initImagePlaceholders: function() {
        document.querySelectorAll('img').forEach(img => {
            img.addEventListener('error', function() {
                this.style.display = 'none';
            });
        });
    },
    /**
     * Règles de séries par niveau
     */
    getSeriesForNiveau: function(niveau) {
        if (!niveau) return null; // null = afficher tout
        if (niveau === '6iem' || niveau === '5iem') return [];           // Pas de série
        if (niveau === '4iem' || niveau === '3iem') return ['ALL', 'ESP', 'CHS'];
        if (niveau === '2nde') return ['A', 'C'];
        if (niveau === '1ere' || niveau === 'Tle') return ['A', 'C', 'D'];
        return null;
    },

    /**
     * Charge les salles depuis l'API et alimente le select
     * @param {string} niveau
     * @param {string} serie
     * @param {HTMLSelectElement} salleSelect
     * @param {string} currentSalle  - valeur à pré-sélectionner si présente
     */
    loadSalles: function(niveau, serie, salleSelect, currentSalle) {
        if (!salleSelect) return;

        // Réinitialiser
        salleSelect.innerHTML = '<option value="">Toutes les salles</option>';

        if (!niveau) {
            salleSelect.disabled = true;
            return;
        }

        const ctx = document.querySelector('meta[name="ctx"]')?.content || '';
        const url = `${ctx}/api/salles?niveau=${encodeURIComponent(niveau)}&serie=${encodeURIComponent(serie || '')}`;

        fetch(url)
            .then(r => r.json())
            .then(salles => {
                salles.forEach(s => {
                    const opt = document.createElement('option');
                    opt.value = s;
                    opt.textContent = s;
                    if (s === currentSalle) opt.selected = true;
                    salleSelect.appendChild(opt);
                });
                salleSelect.disabled = salles.length === 0;
            })
            .catch(() => { salleSelect.disabled = true; });
    },

    /**
     * Initialise les filtres en cascade sur un formulaire donné
     * @param {Object} opts
     *   - niveauId : id du select niveau
     *   - serieId  : id du select série
     *   - salleId  : id du select/input salle
     *   - currentNiveau, currentSerie, currentSalle : valeurs actuelles (depuis serveur)
     */
    initCascadeFilters: function(opts) {
        const niveauSel = document.getElementById(opts.niveauId);
        const serieSel  = document.getElementById(opts.serieId);
        const salleSel  = document.getElementById(opts.salleId);
        if (!niveauSel) return;

        const applyNiveau = (niveau, resetSerie) => {
            const allowed = LyceeAdmin.getSeriesForNiveau(niveau);

            if (serieSel) {
                if (allowed === null) {
                    // Afficher tout
                    Array.from(serieSel.options).forEach((o, i) => { if (i > 0) o.style.display = 'block'; });
                    serieSel.disabled = false;
                    serieSel.style.display = 'block';
                    serieSel.closest('.input-group, div')?.style?.removeProperty('display');
                } else if (allowed.length === 0) {
                    // 6e/5e : pas de série
                    serieSel.value = '';
                    serieSel.disabled = true;
                    // Masquer visuellement le groupe série
                    const grp = document.getElementById(opts.serieGroupId || 'serie-group');
                    if (grp) grp.style.visibility = 'hidden';
                } else {
                    const grp = document.getElementById(opts.serieGroupId || 'serie-group');
                    if (grp) grp.style.visibility = 'visible';
                    serieSel.disabled = false;
                    Array.from(serieSel.options).forEach((o, i) => {
                        if (i === 0) return;
                        o.style.display = allowed.includes(o.value) ? 'block' : 'none';
                        if (!allowed.includes(o.value) && o.selected) serieSel.value = '';
                    });
                }
                if (resetSerie) serieSel.value = '';
            }

            // Charger les salles pour le niveau courant
            const serie = serieSel ? serieSel.value : '';
            LyceeAdmin.loadSalles(niveau, serie, salleSel, resetSerie ? '' : (opts.currentSalle || ''));
        };

        // Quand niveau change
        niveauSel.addEventListener('change', () => applyNiveau(niveauSel.value, true));

        // Quand série change → recharger les salles
        if (serieSel) {
            serieSel.addEventListener('change', () => {
                LyceeAdmin.loadSalles(niveauSel.value, serieSel.value, salleSel, '');
            });
        }

        // Init au chargement (avec valeurs pré-sélectionnées)
        applyNiveau(opts.currentNiveau || niveauSel.value, false);
    },

    /**
     * Filtre les séries selon le niveau sélectionné (Module Classes - ancien comportement)
     */
    updateClasseFilters: function(niveau) {
        const s = document.getElementById('serie-filter');
        if (!s) return;
        Array.from(s.options).forEach((opt, i) => {
            if (i === 0) return;
            const val = opt.value;
            let show = false;
            if (!niveau) {
                show = true;
            } else if (niveau.startsWith('6') || niveau.startsWith('5')) {
                show = (val === 'Général');
            } else if (niveau.startsWith('4') || niveau.startsWith('3')) {
                show = (['ALL', 'Esp', 'Chs'].includes(val));
            } else if (niveau === '2nde') {
                show = (['A', 'C'].includes(val));
            } else {
                show = (['A', 'C', 'D', 'TI'].includes(val));
            }
            opt.style.display = show ? 'block' : 'none';
            if (!show && opt.selected) s.value = "";
        });
    },

    initActionMenus: function() {
        document.querySelectorAll('.action-menu-trigger').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.stopPropagation();
                const menu = btn.closest('.action-menu');
                document.querySelectorAll('.action-menu.open').forEach(m => {
                    if (m !== menu) m.classList.remove('open');
                });
                menu.classList.toggle('open');
            });
        });
        document.addEventListener('click', () => {
            document.querySelectorAll('.action-menu.open').forEach(m => m.classList.remove('open'));
        });
    },

    initNotifications: function() {
        const ctx = document.querySelector('meta[name="ctx"]')?.content || '';
        const toggle = document.getElementById('notif-toggle');
        const dropdown = document.getElementById('notif-dropdown');
        const badge = document.getElementById('notif-badge');
        const list = document.getElementById('notif-list');
        const markAll = document.getElementById('notif-mark-all');
        if (!toggle || !dropdown) return;

        const load = () => {
            fetch(ctx + '/app/notifications')
                .then(r => r.json())
                .then(data => {
                    if (badge) {
                        if (data.unread > 0) {
                            badge.textContent = data.unread > 9 ? '9+' : data.unread;
                            badge.style.display = 'flex';
                        } else {
                            badge.style.display = 'none';
                        }
                    }
                    if (!list) return;
                    list.innerHTML = '';
                    if (!data.items || data.items.length === 0) {
                        list.innerHTML = '<li class="notif-empty">Aucune notification</li>';
                        return;
                    }
                    data.items.forEach(n => {
                        const li = document.createElement('li');
                        li.className = 'notif-item' + (n.lue ? '' : ' unread');
                        li.textContent = n.message;
                        li.addEventListener('click', () => {
                            fetch(ctx + '/app/notifications', {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                                body: 'id=' + n.id
                            }).then(() => { if (n.lien) globalThis.location.href = ctx + n.lien; else load(); });
                        });
                        list.appendChild(li);
                    });
                })
                .catch(() => {});
        };

        toggle.addEventListener('click', (e) => {
            e.stopPropagation();
            const open = !dropdown.hidden;
            dropdown.hidden = open;
            if (!open) load();
        });
        document.addEventListener('click', () => { dropdown.hidden = true; });
        dropdown.addEventListener('click', e => e.stopPropagation());
        if (markAll) {
            markAll.addEventListener('click', () => {
                fetch(ctx + '/app/notifications', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: 'action=readAll'
                }).then(load);
            });
        }
        load();
    },

    initFlashAutoDismiss: function() {
        document.querySelectorAll('[data-flash]').forEach(el => {
            setTimeout(() => el.remove(), 6000);
        });
    },

    initSidebar: function() {
        const sidebar = document.getElementById('sidebar');
        const overlay = document.getElementById('sidebar-overlay');
        const toggle = document.getElementById('sidebar-toggle');
        if (!sidebar || !toggle) return;

        const close = () => {
            sidebar.classList.remove('open');
            if (overlay) overlay.hidden = true;
            document.body.classList.remove('sidebar-open');
        };
        const open = () => {
            sidebar.classList.add('open');
            if (overlay) overlay.hidden = false;
            document.body.classList.add('sidebar-open');
        };

        toggle.addEventListener('click', () => {
            if (sidebar.classList.contains('open')) close();
            else open();
        });
        if (overlay) overlay.addEventListener('click', close);
        window.addEventListener('resize', () => {
            if (globalThis.innerWidth > 1024) close();
        });
        sidebar.querySelectorAll('.nav-link').forEach(link => {
            link.addEventListener('click', () => {
                if (globalThis.innerWidth <= 1024) close();
            });
        });
    }
};

// Auto-init on load
document.addEventListener('DOMContentLoaded', () => {
    LyceeAdmin.initPagination();
    LyceeAdmin.initProgressBars();
    LyceeAdmin.initImagePlaceholders();
    LyceeAdmin.initActionMenus();
    LyceeAdmin.initNotifications();
    LyceeAdmin.initFlashAutoDismiss();
    LyceeAdmin.initSidebar();
    
    // Initialisation spécifique pour le module Classes
    const nf = document.getElementById('niveau-filter');
    if (nf) LyceeAdmin.updateClasseFilters(nf.value);

    // Initialisation du graphique de répartition des séries
    const chart = document.getElementById('series-chart');
    if (chart?.dataset?.gradient) {
        chart.style.background = `conic-gradient(${chart.dataset.gradient})`;
    }

    // Initialisation des pastilles de couleur (Légende)
    document.querySelectorAll('.series-legend-dot').forEach(dot => {
        if (dot.dataset.bg) dot.style.background = dot.dataset.bg;
    });
});

// Export to global scope
globalThis.LyceeAdmin = LyceeAdmin;
